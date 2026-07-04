package com.simplecoding.paymentservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.simplecoding.paymentservice.domain.OrderStatus

data class ConfirmPaymentResponseDto (
    @JsonProperty("order_id")
    val orderId: Long?,

    @JsonProperty("status")
    val status: OrderStatus?,

    @JsonProperty("success")
    val success: Boolean,

    @JsonProperty("message")
    val message: String?,
)