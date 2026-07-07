package com.simplecoding.orderservice.domain.event

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class OrderCreatedEvent(
    val orderId: Long,
    val context: Map<String, String>,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    val timestamp : LocalDateTime,
)