package com.simplecoding.paymentservice.domain.event

import com.simplecoding.paymentservice.dictionary.PaymentStatus

data class PaymentCompletedEvent(
    val orderId: Long?,
    val transactionId: String?,
    val status: PaymentStatus?
)