package com.simplecoding.orderservice.domain.event

import com.simplecoding.orderservice.dictionary.PaymentStatus

data class PaymentCompletedEvent(
    val orderId: Long?,
    val transactionId: String?,
    val status: PaymentStatus?
)