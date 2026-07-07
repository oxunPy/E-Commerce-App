package com.simplecoding.orderservice.domain.event

data class PaymentFailedEvent(
    val orderId: Long?,
    val reason: String?
)
