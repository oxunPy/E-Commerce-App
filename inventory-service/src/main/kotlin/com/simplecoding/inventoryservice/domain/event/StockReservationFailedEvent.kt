package com.simplecoding.inventoryservice.domain.event

data class StockReservationFailedEvent(
    val orderId: Long?,
    val reason: String?
)