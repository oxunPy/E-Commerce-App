package com.simplecoding.inventoryservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class CancelReservationRequestDto(
    @JsonProperty("order_id")
    @NotNull(message = "Ид заказа не может быт нулевым")
    var orderId: Long?,
)