package com.simplecoding.paymentservice.client

import com.simplecoding.paymentservice.domain.dto.CompleteReservationRequestDto
import com.simplecoding.paymentservice.domain.dto.CompleteReservationResponseDto
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class InventoryClient (
    private val inventoryWebClient: WebClient,
) {

    fun reserveComplete(request: CompleteReservationRequestDto): Mono<CompleteReservationResponseDto> {
        return inventoryWebClient.put()
            .uri("/api/v1/inventory/reserve/complete")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(CompleteReservationResponseDto::class.java)
    }
}