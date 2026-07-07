package com.simplecoding.inventoryservice.domain.event

import com.simplecoding.inventoryservice.dictionary.PaymentStatus
import java.math.BigDecimal

data class PaymentCompletedEvent(
    val orderId: Long?,
    val transactionId: Long?,
    val amount: BigDecimal,
    val status: PaymentStatus?
)