package com.simplecoding.paymentservice.usecase

import com.simplecoding.paymentservice.client.OrderClient
import com.simplecoding.paymentservice.domain.dto.ConfirmPaymentRequestDto
import com.simplecoding.paymentservice.exception.OrderConfirmationFailedException
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import io.github.resilience4j.timelimiter.annotation.TimeLimiter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ConfirmOrderUseCase(
    private val orderClient: OrderClient
) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @CircuitBreaker(
        name = "orderService",
        fallbackMethod = "fallbackCircuitBreakerConfirmOrder"
    )
    @TimeLimiter(name = "orderService")
    @Retry(name = "orderService")
    @Bulkhead(
        name = "orderService",
        fallbackMethod = "fallbackBulkheadConfirmOrder"
    )
    fun confirmOrder(request: ConfirmPaymentRequestDto) {
        orderClient.confirm(request)
            .toFuture()
            .whenCompleteAsync { response, error ->
                if (error != null) {
                    log.error(error.message)
                }

                else if (response != null && response.success) {
                    log.debug("Successfully confirmed order")
                }
            }.exceptionally { throwable ->
                val cause = throwable.cause ?: throwable
                log.error("Техническая ошибка при вызове сервиса order: {}", cause.message)
                throw OrderConfirmationFailedException("order-service error: ${cause.message}")
            }
    }

    fun fallbackCircuitBreakerConfirmOrder(request: ConfirmPaymentRequestDto, t: Throwable) {
        log.error(
            "[order-service] CircuitBreaker: не удалось подвердить заказ по orderId: {}, причина: {}",
            request.orderId,
            t.message
        )
    }

    fun fallbackBulkheadConfirmOrder(request: ConfirmPaymentRequestDto, t: Throwable) {
        log.warn("[order-service] Bulkhead: переполен для orderId: {}, причина: {}", request.orderId, t.message)
    }
}