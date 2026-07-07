package com.simplecoding.inventoryservice.domain.event

data class PaymentFailedEvent(
    val orderId: Long?,
    val reason: String?
)
