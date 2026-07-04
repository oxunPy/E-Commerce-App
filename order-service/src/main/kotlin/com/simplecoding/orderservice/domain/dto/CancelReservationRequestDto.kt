package com.simplecoding.orderservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CancelReservationRequestDto(
    @JsonProperty("order_id")
    var orderId: Long,
)