package com.simplecoding.orderservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class PaymentRequestDto(
    @JsonProperty("order_id")
    var orderId: Long?,

    @JsonProperty("amount")
    val amount: BigDecimal
)