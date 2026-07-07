package com.simplecoding.inventoryservice.domain.dto

data class OrderItemDto(
    val id: Long?,
    val orderId: Long?,
    val productId: String?,
    val quantity: Int
)