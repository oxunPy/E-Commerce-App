package com.simplecoding.paymentservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CreatePaymentRequestDto(
    @JsonProperty("order_id")
    @NotNull(message = "Ид заказа нелзя быт нулевым")
    var orderId: Long?,

    @JsonProperty("amount")
    @DecimalMin(value = "0.001", message = "Сумма платежа менще минималного порога")
    val amount: BigDecimal
)