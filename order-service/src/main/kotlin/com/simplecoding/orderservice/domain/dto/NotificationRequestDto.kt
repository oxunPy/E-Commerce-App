package com.simplecoding.orderservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NotificationRequestDto(
    @JsonProperty("order_id")
    val orderId: Long,

    @JsonProperty("event")
    val event: String
)