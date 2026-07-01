package com.simplecoding.orderservice.client

import com.simplecoding.orderservice.domain.dto.PaymentRequestDto
import com.simplecoding.orderservice.domain.dto.PaymentResponseDto
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Component
class PaymentClient(
    private val paymentWebClient: WebClient
) {
    fun processPayment(orderId: Long, amount: BigDecimal): Mono<PaymentResponseDto> {
        return paymentWebClient.post()
            .uri("/api/v1/payments/process")
            .bodyValue(PaymentRequestDto(orderId, amount))
            .retrieve()
            .bodyToMono(PaymentResponseDto::class.java)

    }
}