package com.simplecoding.paymentservice.client

import com.simplecoding.paymentservice.domain.dto.ConfirmPaymentRequestDto
import com.simplecoding.paymentservice.domain.dto.ConfirmPaymentResponseDto
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class OrderClient(
    private val orderWebClient: WebClient,
) {
    fun confirm(request: ConfirmPaymentRequestDto): Mono<ConfirmPaymentResponseDto> {
        return orderWebClient.put()
            .uri("/api/v1/orders/confirm")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ConfirmPaymentResponseDto::class.java)
    }
}