package com.simplecoding.inventoryservice.service

import com.simplecoding.inventoryservice.domain.dto.CancelReservationRequestDto
import com.simplecoding.inventoryservice.domain.dto.CancelReservationResponseDto
import com.simplecoding.inventoryservice.domain.dto.CompleteReservationRequestDto
import com.simplecoding.inventoryservice.domain.dto.CompleteReservationResponseDto
import com.simplecoding.inventoryservice.domain.dto.CreateInventoryRequestDto
import com.simplecoding.inventoryservice.domain.dto.ReserveProductRequestDto
import com.simplecoding.inventoryservice.domain.entity.Inventory
import com.simplecoding.inventoryservice.domain.entity.InventoryReservation
import com.simplecoding.inventoryservice.exception.NotEnoughItemException
import com.simplecoding.inventoryservice.exception.NotFoundException
import com.simplecoding.inventoryservice.exception.ReserveProductException
import com.simplecoding.inventoryservice.repository.InventoryRepository
import com.simplecoding.inventoryservice.repository.InventoryReservationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Random
import java.util.concurrent.atomic.AtomicBoolean

@Service
class InventoryServiceImpl(
    private val inventoryRepository: InventoryRepository,
    private val inventoryReservationRepository: InventoryReservationRepository
) : InventoryService {

    private val random = Random()
    private val failureMode = AtomicBoolean(false)

    companion object {
        private val log = LoggerFactory.getLogger(InventoryServiceImpl::class.java)
    }

    override fun create(request: CreateInventoryRequestDto): Inventory {
        val inventory = Inventory(request.productId!!, request.quantity!!)
        inventoryRepository.save(inventory)
        return inventory
    }

    override fun reserve(request: ReserveProductRequestDto): InventoryReservation {
        try {
            log.debug("В метод InventoryService.reserve получен запрос: {}", request)
            runFail()

            val inventory = inventoryRepository
                .findByProductId(request.productId!!)
                .orElseThrow { NotFoundException("Инвертар не найдено") }

            if (inventory.getActualQuantity() < request.quantity) {
                throw NotEnoughItemException("Недостаточно товаров для вашего запроса")
            }

            val reservation = InventoryReservation(
                productId = request.productId,
                quantity = request.quantity,
                inventory = inventory
            )

            inventoryReservationRepository.save(reservation)
            return reservation
        } catch (e: Exception) {
            val cause = e.cause ?: e
            log.error("Ошибка возникла при резервирование продукта: {}", request, e)
            throw ReserveProductException("Failed to reserve product: ${request.productId}")
        }
    }

    override fun completeReserve(request: CompleteReservationRequestDto): CompleteReservationResponseDto {
        throw NotImplementedError()
    }

    override fun cancelReservation(request: CancelReservationRequestDto): CancelReservationResponseDto {
        throw NotImplementedError()
    }

    fun Inventory.getActualQuantity(): Int {
        val now = LocalDateTime.now()
        val reservations = inventoryReservationRepository.findAllByInventoryId(this.id!!)
        val reservedQuantity = reservations
            .filter { it.expiresAt?.isAfter(now) == true }
            .map { it.quantity }
            .reduce { acc, element -> acc + element }

        return this.quantity - reservedQuantity
    }

    private fun runFail() {
        if (failureMode.get()) {
            val randomInt = random.nextInt(100)

            log.debug("Выпало число: {}", randomInt);

            if (randomInt < 30) {
                log.error("Возникли проблемы с обработкой сохранения заказа");
                throw RuntimeException("Типа проблемы с обработкой заказа");
            }

            if (randomInt > 70) {
                log.warn("OrderService замедлился");
                Thread.sleep(200);
            }
        }
    }
}