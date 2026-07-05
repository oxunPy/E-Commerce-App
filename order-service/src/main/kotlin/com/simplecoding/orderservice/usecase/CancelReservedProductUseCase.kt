package com.simplecoding.orderservice.usecase

import com.simplecoding.orderservice.client.InventoryClient
import com.simplecoding.orderservice.domain.dto.CancelReservationResponseDto
import com.simplecoding.orderservice.exception.InventoryReservationFailedException
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import io.github.resilience4j.timelimiter.annotation.TimeLimiter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture


@Component
class CancelReservedProductUseCase(
    private val inventoryClient: InventoryClient
) {

    companion object {
        private val log = LoggerFactory.getLogger(CancelReservedProductUseCase::class.java)
    }

    @CircuitBreaker(
        name = "inventoryService",
        fallbackMethod = "fallbackCircuitBreakerCancelReservedProduct"
    )
    @TimeLimiter(name = "inventoryService")
    @Retry(name = "inventoryService")
    @Bulkhead(
        name = "inventoryService",
        fallbackMethod = "fallbackBulkheadCancelReservedProduct"
    )
    fun cancelReservedProduct(orderId: Long): CompletableFuture<CancelReservationResponseDto?> {
        return inventoryClient.reserveCancel(orderId)
            .toFuture()
            .exceptionally { throwable ->
                val cause = throwable.cause ?: throwable
                log.error("Техническая ошибка при вызове сервиса inventory: {}", cause.message)
                throw InventoryReservationFailedException("inventory-service error: ${cause.message}")
            }
    }

    fun fallbackCircuitBreakerCancelReservedProduct(
        orderId: Long,
        t: Throwable
    ): CompletableFuture<CancelReservationResponseDto?> {
        log.error("[inventory-service] CircuitBreaker: не удалось отменить резервацию для orderId={}, причина: {}", orderId, t.message)
        return CompletableFuture.completedFuture(
            CancelReservationResponseDto(
                orderId = null,
                message = "[inventory-service] Сервис временно недоступен",
                success = false
            )
        )
    }

    fun fallbackBulkheadCancelReservedProduct(
        orderId: Long,
        t: Throwable
    ): CompletableFuture<CancelReservationResponseDto?> {
        log.warn("[inventory-service] Bulkhead: переполнен для orderId={}, причина: {}", orderId, t.message)
        return CompletableFuture.completedFuture(
            CancelReservationResponseDto(
                orderId = null,
                message = "[inventory-service] Сервис временно недоступен",
                success = false
            )
        )
    }
}