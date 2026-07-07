package com.simplecoding.orderservice.domain.event

import com.simplecoding.orderservice.dictionary.PaymentStatus
import java.math.BigDecimal

data class PaymentCompletedEvent(
    val orderId: Long?,
    val transactionId: Long?,
    val amount: BigDecimal,
    val status: PaymentStatus?
)