package com.simplecoding.paymentservice.domain.event

import java.math.BigDecimal

data class StockReservedEvent(
    val orderId: Long?,

    val amount: BigDecimal,

    val transactionId: Long?,

    val inventoryId: Long?,

    val products: List<String?>,
)