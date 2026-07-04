package com.simplecoding.orderservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CompleteReservationRequestDto(
    @JsonProperty("order_id")
    var orderId: Long?
)