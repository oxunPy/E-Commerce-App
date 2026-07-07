package com.simplecoding.orderservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.simplecoding.orderservice.domain.entity.Order
import com.simplecoding.orderservice.dictionary.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateOrderResponseDto(
    @JsonProperty("id")
    val id: Long?,

    @JsonProperty("amount")
    val amount: BigDecimal?,

    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,

    @JsonProperty("status")
    val status: OrderStatus?,

    @JsonProperty("items")
    val items: List<OrderItemResponseDto>,

    @JsonProperty("checkout_url")
    val checkoutUrl: String?,
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
            },
            checkoutUrl = order.checkoutUrl,
        ) else null
    }

    data class OrderItemResponseDto(
        val productId: String?,
        val productName: String?,
        val quantity: Int?,
        val price: BigDecimal?,
        val itemTotal: BigDecimal?,
    )
}
