package com.simplecoding.paymentservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class CheckoutPaymentRequestDto(
    @JsonProperty("payment_id")
    val paymentId: Long?,

    @JsonProperty("order_id")
    val orderId: Long?,

    @JsonProperty("amount")
    val amount: BigDecimal?,
)
