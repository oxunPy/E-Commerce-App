package com.simplecoding.orderservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class InventoryReservationRequestDto(
    @JsonProperty("product_id")
    val productId: String?,

    @JsonProperty("order_id")
    var orderId: Long?,

    @JsonProperty("quantity")
    val quantity: Int
)