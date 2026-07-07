package com.simplecoding.inventoryservice.domain.event

import java.math.BigDecimal

data class StockReservedEvent(
    val orderId: Long?,

    val amount: BigDecimal,

    val products: List<String?>,
)