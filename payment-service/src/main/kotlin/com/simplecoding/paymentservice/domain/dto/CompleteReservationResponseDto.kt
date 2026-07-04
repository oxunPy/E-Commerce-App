package com.simplecoding.paymentservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CompleteReservationResponseDto(
    @JsonProperty("order_id")
    val orderId: Long?,

    @JsonProperty("success")
    val success: Boolean,

    @JsonProperty("message")
    val message: String
)