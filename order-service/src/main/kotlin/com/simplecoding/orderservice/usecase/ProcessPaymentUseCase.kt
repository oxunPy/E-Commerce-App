package com.simplecoding.orderservice.usecase

import com.simplecoding.orderservice.client.PaymentClient
import com.simplecoding.orderservice.dictionary.PaymentStatus
import com.simplecoding.orderservice.domain.dto.PaymentResponseDto
import com.simplecoding.orderservice.exception.PaymentFailedException
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import io.github.resilience4j.timelimiter.annotation.TimeLimiter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Component
class ProcessPaymentUseCase(
    private val paymentClient: PaymentClient,
) {

    companion object {
        private val log = LoggerFactory.getLogger(ProcessPaymentUseCase::class.java)
    }

    @CircuitBreaker(
        name = "paymentService",
        fallbackMethod = "fallbackCircuitBreakerProcessPayment"
    )
    @TimeLimiter(name = "paymentService")
    @Retry(name = "paymentService")
    @Bulkhead(
        name = "paymentService",
        fallbackMethod = "fallbackBulkheadProcessPayment"
    )
    fun processPayment(orderId: Long, amount: BigDecimal): CompletableFuture<PaymentResponseDto?> {
        return paymentClient.processPayment(orderId, amount)
            .toFuture()
            .exceptionally { throwable ->
                val cause = throwable.cause ?: throwable
                log.error("Техническая ошибка при вызове сервиса payment {}", cause.message)
                throw PaymentFailedException("payment-service error: ${cause.message}")
            }
    }

    fun fallbackCircuitBreakerProcessPayment(
        orderId: Long,
        amount: BigDecimal,
        t: Throwable
    ): CompletableFuture<PaymentResponseDto?> {
        log.error("[payment-service] CircuitBreaker: не удалось выполнить оплату для orderId={}, причина: {}", orderId, t.message)
        return CompletableFuture.completedFuture(
            PaymentResponseDto(
                paymentId = null,
                orderId = orderId,
                status = PaymentStatus.CANCELLED,
                createdAt = LocalDateTime.now(),
                amount = amount,
                checkoutUrl = null
            )
        )
    }

    fun fallbackBulkheadProcessPayment(
        orderId: Long,
        amount: BigDecimal,
        t: Throwable
    ): CompletableFuture<PaymentResponseDto?> {
        log.warn("[payment-service] Bulkhead: переполнен для orderId={}, причина: {}", orderId, t.message)
        return CompletableFuture.completedFuture(
            PaymentResponseDto(
                paymentId = null,
                orderId = orderId,
                status = PaymentStatus.CANCELLED,
                createdAt = LocalDateTime.now(),
                amount = amount,
                checkoutUrl = null
            )
        )
    }
}