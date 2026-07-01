package com.simplecoding.inventoryservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CancelReservationResponseDto(
    @JsonProperty("order_id")
    val orderId: Long,

    @JsonProperty("message")
    val message: String,

    @JsonProperty("success")
    val success: Boolean,
)