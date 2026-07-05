package com.simplecoding.orderservice.client

import com.simplecoding.orderservice.domain.dto.CancelReservationRequestDto
import com.simplecoding.orderservice.domain.dto.CancelReservationResponseDto
import com.simplecoding.orderservice.domain.dto.CompleteReservationRequestDto
import com.simplecoding.orderservice.domain.dto.CompleteReservationResponseDto
import com.simplecoding.orderservice.domain.dto.InventoryReservationRequestDto
import com.simplecoding.orderservice.domain.dto.InventoryReservationResponseDto
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class InventoryClient(
    private val inventoryWebClient: WebClient,
) {

    fun reserve(productId: String, orderId: Long, quantity: Int): Mono<InventoryReservationResponseDto> {
        return inventoryWebClient.post()
            .uri("/api/v1/inventory/reserve")
            .bodyValue(InventoryReservationRequestDto(productId, orderId, quantity))
            .retrieve()
            .bodyToMono(InventoryReservationResponseDto::class.java)
    }

    fun reserveCancel(orderId: Long): Mono<CancelReservationResponseDto> {
        return inventoryWebClient.put()
            .uri("/api/v1/inventory/reserve/cancel")
            .bodyValue(CancelReservationRequestDto(orderId = orderId))
            .retrieve()
            .bodyToMono(CancelReservationResponseDto::class.java)
    }

}