package com.simplecoding.orderservice.domain.dto

import com.simplecoding.orderservice.domain.entity.Order
import com.simplecoding.orderservice.domain.entity.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponseDto(
    val id: Long?,
    val amount: BigDecimal?,
    val createdAt: LocalDateTime?,
    val status: OrderStatus?,
) {
    companion object {
        fun fromOrder(order: Order) = OrderResponseDto(
            id = order.id,
            amount = order.amount,
            createdAt = order.createdAt,
            status = order.status,
        )
    }
}
