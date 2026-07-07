package com.simplecoding.orderservice.domain.event

data class StockReservedEvent(
    val orderId: Long?,

    val transactionId: Long?,

    val inventoryId: Long?,

    val products: List<String>?,
)