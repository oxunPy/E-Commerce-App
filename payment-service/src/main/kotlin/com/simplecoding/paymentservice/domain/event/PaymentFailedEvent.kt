package com.simplecoding.paymentservice.domain.event

data class PaymentFailedEvent(
    val orderId: Long?,
    val reason: String?
)
