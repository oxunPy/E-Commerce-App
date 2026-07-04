package com.simplecoding.paymentservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class CheckoutPaymentResponseDto(
    @JsonProperty("payment_id")
    val paymentId: Long?,

    @JsonProperty("order_id")
    val orderId: Long?,

    @JsonProperty("amount")
    val amount: BigDecimal?,

    @JsonProperty("success")
    val success: Boolean,

    @JsonProperty("message")
    val message: String?,
)