package com.simplecoding.paymentservice.usecase

import com.simplecoding.paymentservice.client.InventoryClient
import com.simplecoding.paymentservice.domain.dto.CompleteReservationRequestDto
import com.simplecoding.paymentservice.exception.ReservationCompleteFailedException
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import io.github.resilience4j.timelimiter.annotation.TimeLimiter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ReserveCompleteUseCase(
    private val inventoryClient: InventoryClient
) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }


    @CircuitBreaker(
        name = "inventoryService",
        fallbackMethod = "fallbackCircuitBreakerReserveComplete"
    )
    @TimeLimiter(name = "inventoryService")
    @Retry(name = "inventoryService")
    @Bulkhead(
        name = "inventoryService",
        fallbackMethod = "fallbackBulkheadReserveComplete"
    )
    fun reserveComplete(request: CompleteReservationRequestDto) {
        inventoryClient.reserveComplete(request)
            .toFuture()
            .whenCompleteAsync { response, error ->
                if (error != null) {
                    log.error("Reserve complete error", error)
                }

                else if (response != null && response.success) {
                    log.debug("Reserve complete success")
                }
            }.exceptionally { throwable ->
                val cause = throwable.cause ?: throwable
                log.error("Техническая ошибка при вызове сервиса inventory: {}", cause.message)
                throw ReservationCompleteFailedException("inventory-service error: ${cause.message}")
            }
    }

    fun fallbackCircuitBreakerReserveComplete(request: CompleteReservationRequestDto, t: Throwable) {
        log.error(
            "[inventory-service] CircuitBreaker: не удалось заверщить резервирование товара для orderId: {}, причина: {}",
            request.orderId,
            t.message
        )
    }

    fun fallbackBulkheadReserveComplete(request: CompleteReservationRequestDto, t: Throwable) {
        log.warn("[inventory-service] Bulkhead: переполен для orderId: {}, причина: {}", request.orderId, t.message)
    }
}