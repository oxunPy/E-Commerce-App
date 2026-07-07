package com.simplecoding.paymentservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.simplecoding.paymentservice.dictionary.PaymentStatus
import java.math.BigDecimal

data class ConfirmPaymentRequestDto(
    @JsonProperty("order_id")
    val orderId: Long?,

    @JsonProperty("payment_id")
    val paymentId: Long?,

    @JsonProperty("amount")
    val amount: BigDecimal,

    @JsonProperty("status")
    val status: PaymentStatus,
)