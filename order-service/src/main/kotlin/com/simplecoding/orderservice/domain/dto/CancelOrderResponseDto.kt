package com.simplecoding.orderservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CancelOrderResponseDto(
    @JsonProperty("order_id")
    val orderId: Long?,

    @JsonProperty("success")
    val success: Boolean,

    @JsonProperty("message")
    val message: String?
)