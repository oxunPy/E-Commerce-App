package com.simplecoding.orderservice.domain.event

data class StockReservationFailedEvent(
    val orderId: Long?,
    val reason: String?
)