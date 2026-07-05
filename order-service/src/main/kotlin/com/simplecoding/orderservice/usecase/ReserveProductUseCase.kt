package com.simplecoding.orderservice.usecase

import com.simplecoding.orderservice.client.InventoryClient
import com.simplecoding.orderservice.domain.dto.InventoryReservationResponseDto
import com.simplecoding.orderservice.exception.InventoryReservationFailedException
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import io.github.resilience4j.timelimiter.annotation.TimeLimiter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class ReserveProductUseCase(
    private val inventoryClient: InventoryClient
) {

    companion object {
        private val log = LoggerFactory.getLogger(ReserveProductUseCase::class.java)
    }

    @CircuitBreaker(
        name = "inventoryService",
        fallbackMethod = "fallbackCircuitBreakerReserveProduct")
    @TimeLimiter(name = "inventoryService")
    @Retry(name = "inventoryService")
    @Bulkhead(
        name = "inventoryService",
        fallbackMethod = "fallbackBulkheadReserveProduct")
    fun reserveProduct(
        productId: String,
        orderId: Long,
        quantity: Int
    ): CompletableFuture<InventoryReservationResponseDto?> {
         return inventoryClient.reserve(productId, orderId, quantity)
             .toFuture()
             .exceptionally { throwable ->
                 val cause = throwable.cause ?: throwable
                 log.error("Техническая ошибка при вызове сервисе inventory: {}", cause.message)
                 throw InventoryReservationFailedException("inventory-service error: ${cause.message}")
             }
    }

    fun fallbackCircuitBreakerReserveProduct(
        productId: String,
        orderId: Long,
        quantity: Int,
        t: Throwable
    ): CompletableFuture<InventoryReservationResponseDto?> {
        log.error(
            "[inventory-service] CircuitBreaker: не удалось резервировать товар для orderId: {}, productId: {}",
            orderId,
            productId
        )
        return CompletableFuture.completedFuture(
            InventoryReservationResponseDto(
                id = null,
                inventoryId = null,
                productId = productId,
                quantity = quantity,
            )
        )
    }

    fun fallbackBulkheadReserveProduct(
        productId: String,
        orderId: Long,
        quantity: Int,
        t: Throwable
    ): CompletableFuture<InventoryReservationResponseDto?> {
        log.warn("[inventory-service] Bulkhead: переполен для orderId: {}, productId: {}", orderId, productId)
        return CompletableFuture.completedFuture(
            InventoryReservationResponseDto(
                id = null,
                inventoryId = null,
                productId = productId,
                quantity = quantity,
            )
        )
    }
}