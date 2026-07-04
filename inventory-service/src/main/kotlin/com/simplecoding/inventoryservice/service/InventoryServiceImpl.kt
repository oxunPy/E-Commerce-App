package com.simplecoding.inventoryservice.service

import com.simplecoding.inventoryservice.domain.dto.CancelReservationRequestDto
import com.simplecoding.inventoryservice.domain.dto.CancelReservationResponseDto
import com.simplecoding.inventoryservice.domain.dto.CompleteReservationRequestDto
import com.simplecoding.inventoryservice.domain.dto.CompleteReservationResponseDto
import com.simplecoding.inventoryservice.domain.dto.CreateInventoryRequestDto
import com.simplecoding.inventoryservice.domain.dto.CreateInventoryResponseDto
import com.simplecoding.inventoryservice.domain.dto.InventoryDto
import com.simplecoding.inventoryservice.domain.dto.InventoryReservationResponseDto
import com.simplecoding.inventoryservice.domain.dto.InventoryReservationRequestDto
import com.simplecoding.inventoryservice.domain.entity.Inventory
import com.simplecoding.inventoryservice.domain.entity.InventoryHistory
import com.simplecoding.inventoryservice.domain.entity.InventoryReservation
import com.simplecoding.inventoryservice.exception.CancelReservationException
import com.simplecoding.inventoryservice.exception.NotEnoughItemException
import com.simplecoding.inventoryservice.exception.NotFoundException
import com.simplecoding.inventoryservice.exception.ReserveProductException
import com.simplecoding.inventoryservice.repository.InventoryHistoryRepository
import com.simplecoding.inventoryservice.repository.InventoryRepository
import com.simplecoding.inventoryservice.repository.InventoryReservationRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.util.Random
import java.util.concurrent.atomic.AtomicBoolean

@Service
class InventoryServiceImpl(
    private val inventoryRepository: InventoryRepository,
    private val inventoryHistoryRepository: InventoryHistoryRepository,
    private val inventoryReservationRepository: InventoryReservationRepository,
    private val redisTemplate: RedisTemplate<String, String>
) : InventoryService {

    private val random = Random()
    private val failureMode = AtomicBoolean(false)

    companion object {
        private val log = LoggerFactory.getLogger(InventoryServiceImpl::class.java)
    }

    @Transactional
    override fun create(request: CreateInventoryRequestDto): CreateInventoryResponseDto {
        log.debug("В метод InventoryService.create получен запрос: {}", request)

        val inventory = inventoryRepository.findByProductId(request.productId!!)
            .orElse(
                Inventory(
                    request.productId,
                    0
                )
            ).apply {
                this.quantity += request.quantity!!
                inventoryRepository.save(this).let {
                    this.id = it.id
                }
            }

        inventoryHistoryRepository.save(
            InventoryHistory(
                productId = request.productId,
                quantity = request.quantity!!,
                inventory = inventory,
                transactionType = InventoryHistory.TransactionType.STOCK_IN
            )
        )

        log.debug("Инвертар создан с Ид: {}", inventory.id)
        return CreateInventoryResponseDto.fromInventory(inventory)
    }

    override fun reserve(request: InventoryReservationRequestDto): InventoryReservationResponseDto {
        try {
            log.debug("В метод InventoryService.reserve получен запрос: {}", request)
            runFail()

            val inventory = inventoryRepository
                .findByProductId(request.productId!!)
                .orElseThrow { NotFoundException("Инвертар не найдено") }

            if (inventory.getActualQuantity() < request.quantity) {
                throw NotEnoughItemException("Недостаточно товаров для вашего запроса")
            }

            val reservation = inventoryReservationRepository.save(
                InventoryReservation(
                    productId = request.productId,
                    quantity = request.quantity,
                    inventory = inventory,
                    orderId = request.orderId!!,
                )
            )

            redisTemplate.opsForValue().set(
                "reservation:expire:${reservation.id}",
                reservation.id.toString(),
                Duration.ofMinutes(5)
            )

            return InventoryReservationResponseDto.fromInventoryReservation(reservation)
        } catch (e: Exception) {
            val cause = e.cause ?: e
            log.error("Ошибка возникла при резервирование продукта: {}", cause.message)
            throw ReserveProductException("Failed to reserve product: ${request.productId}")
        }
    }

    @Transactional
    override fun completeReserve(request: CompleteReservationRequestDto): CompleteReservationResponseDto {
        try {
            val reservations = inventoryReservationRepository.findAllByOrderId(
                request.orderId!!
            )

            if (reservations
                .any { it.expiresAt!!.isBefore(LocalDateTime.now()) }) {
                throw RuntimeException("Reservation product timeout")
            }

            reservations.forEach { reservation ->
                redisTemplate.opsForValue().getAndExpire("reservation:expire:${reservation.id}", Duration.ofSeconds(3))
                val inventory = inventoryRepository.findByProductId(reservation.productId!!).orElseThrow {
                    NotFoundException("Inventory not found at product id: ${reservation.productId}")
                }

                if (inventory.quantity < reservation.quantity) {
                    throw RuntimeException("Inventory quantity less: ${inventory.quantity} < ${reservation.quantity}")
                }

                inventory.quantity -= reservation.quantity
                inventoryRepository.save(inventory)
            }

            return CompleteReservationResponseDto(
                orderId = request.orderId,
                success = true,
                message = "Order reservation completed successfully!"
            )
        } catch (e: Exception) {
            val cause = e.cause ?: e
            log.error("Ошибка при завершение резервации", cause)
            throw ReserveProductException("Failed to reserve product: ${cause.message}")
        }
    }

    override fun cancelReservation(request: CancelReservationRequestDto): CancelReservationResponseDto {
        try {
            val reservations = inventoryReservationRepository.findAllByOrderId(
                request.orderId!!
            )

            reservations.forEach { reservation ->
                redisTemplate.opsForValue().getAndExpire("reservation:expire:${reservation.id}", Duration.ofSeconds(3))
            }

            return CancelReservationResponseDto(
                orderId = request.orderId,
                success = true,
                message = "Order reservataions cancelled",
            )

        } catch (e: Exception) {
            val cause = e.cause ?: e
            log.error("Ошибка при отменение резервации", cause)
            throw CancelReservationException("Failed to cancel reservation: ${cause.message}")
        }
    }

    override fun getInventoryList(): List<InventoryDto> {
        return inventoryRepository.findAll().map { InventoryDto.fromInventory(it) }
    }

    private fun Inventory.getActualQuantity(): Int {
        val now = LocalDateTime.now()
        val reservations = inventoryReservationRepository.findAllByInventoryId(this.id!!)
        val reservedQuantity = reservations
            .filter { it.expiresAt?.isAfter(now) == true }
            .sumOf { it.quantity }

        return this.quantity - reservedQuantity
    }

    private fun runFail() {
        if (failureMode.get()) {
            val randomInt = random.nextInt(100)

            log.debug("Выпало число: {}", randomInt)

            if (randomInt < 30) {
                log.error("Возникли проблемы с обработкой сохранения заказа")
                throw RuntimeException("Типа проблемы с обработкой заказа")
            }

            if (randomInt > 70) {
                log.warn("OrderService замедлился")
                Thread.sleep(200)
            }
        }
    }
}