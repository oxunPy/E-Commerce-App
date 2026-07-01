package com.simplecoding.orderservice.domain.dto

import com.simplecoding.orderservice.domain.entity.Order
import com.simplecoding.orderservice.domain.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateOrderResponseDto(
    val id: Long?,
    val amount: BigDecimal?,
    val createdAt: LocalDateTime?,
    val status: OrderStatus?,
    val items: List<OrderItemResponseDto>
) {
    companion object {
        fun fromOrder(order: Order?) = if (order != null) CreateOrderResponseDto(
            id = order.id,
            amount = order.amount,
            createdAt = order.createdAt,
            status = order.status,
            items = order.items.map {
                OrderItemResponseDto(
                    productId = it.productId,
                    productName = it.productName,
                    quantity = it.quantity,
                    price = it.price,
                    itemTotal = (it.price ?: BigDecimal.ZERO).multiply(BigDecimal(it.quantity ?: 0))
                )
            }
        ) else null
    }

    data class OrderItemResponseDto(
        val productId: Long?,
        val productName: String?,
        val quantity: Int?,
        val price: BigDecimal?,
        val itemTotal: BigDecimal?,
    )
}
