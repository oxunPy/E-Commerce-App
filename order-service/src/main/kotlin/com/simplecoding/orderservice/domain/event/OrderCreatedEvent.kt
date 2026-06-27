package com.simplecoding.orderservice.domain.event

import java.time.LocalDateTime

data class OrderCreatedEvent(
    val orderId: Long,
    val context: Map<String, String>,
    val timestamp : LocalDateTime,
)