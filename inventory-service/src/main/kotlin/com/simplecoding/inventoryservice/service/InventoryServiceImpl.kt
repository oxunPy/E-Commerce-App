package com.simplecoding.inventoryservice.service

import com.simplecoding.inventoryservice.domain.dto.CancelReservationRequestDto
import com.simplecoding.inventoryservice.domain.dto.CancelReservationResponseDto
import com.simplecoding.inventoryservice.domain.dto.CompleteReservationRequestDto
import com.simplecoding.inventoryservice.domain.dto.CompleteReservationResponseDto
import com.simplecoding.inventoryservice.domain.dto.CreateInventoryRequestDto
import com.simplecoding.inventoryservice.domain.dto.CreateInventoryResponseDto
import com.simplecoding.inventoryservice.domain.dto.InventoryDto
import com.simplecoding.inventoryservice.domain.dto.InventoryReservationDto
import com.simplecoding.inventoryservice.domain.dto.ReserveProductRequestDto
import com.simplecoding.inventoryservice.domain.entity.Inventory
import com.simplecoding.inventoryservice.domain.entity.InventoryHistory
import com.simplecoding.inventoryservice.domain.entity.InventoryReservation
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

    override fun reserve(request: ReserveProductRequestDto): InventoryReservationDto {
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
                Duration.ofMinutes(2)
            )

            return InventoryReservationDto.fromInventoryReservation(reservation)
        } catch (e: Exception) {
            val cause = e.cause ?: e
            log.error("Ошибка возникла при резервирование продукта: {}", cause.message)
            throw ReserveProductException("Failed to reserve product: ${request.productId}")
        }
    }

    override fun completeReserve(request: CompleteReservationRequestDto): CompleteReservationResponseDto {
        throw NotImplementedError()
    }

    override fun cancelReservation(request: CancelReservationRequestDto): CancelReservationResponseDto {
        throw NotImplementedError()
    }

    override fun getInventoryList(): List<InventoryDto> {
        return inventoryRepository.findAll().map { InventoryDto.fromInventory(it) }
    }

    fun Inventory.getActualQuantity(): Int {
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