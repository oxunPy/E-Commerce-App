package com.simplecoding.paymentservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class CompleteReservationRequestDto(
    @JsonProperty("order_id")
    @NotNull(message = "Ид заказа не может быт нулевым")
    var orderId: Long?
)