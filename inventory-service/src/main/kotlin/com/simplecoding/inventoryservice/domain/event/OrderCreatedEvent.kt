package com.simplecoding.inventoryservice.domain.event

import com.fasterxml.jackson.annotation.JsonFormat
import com.simplecoding.inventoryservice.domain.dto.OrderItemDto
import java.time.LocalDateTime

data class OrderCreatedEvent(
    val orderId: Long,
    val context: Map<String, String>,
    val items: List<OrderItemDto>,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    val timestamp : LocalDateTime,
)