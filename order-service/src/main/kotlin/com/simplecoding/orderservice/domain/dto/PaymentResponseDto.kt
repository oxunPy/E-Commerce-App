package com.simplecoding.orderservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.simplecoding.orderservice.domain.PaymentStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentResponseDto(
    @JsonProperty("payment_id")
    val paymentId: Long?,

    @JsonProperty("order_id")
    val orderId: Long?,

    @JsonProperty("status")
    val status: PaymentStatus,

    @JsonProperty("created_at")
    val createdAt: LocalDateTime,

    @JsonProperty("amount")
    val amount: BigDecimal?,
)
