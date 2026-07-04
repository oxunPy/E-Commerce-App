package com.simplecoding.orderservice.domain.entity

import com.simplecoding.orderservice.domain.OrderStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "orders")
class Order() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "order_uuid", nullable = false)
    var orderUuid: String? = null

    @Column(name = "amount", nullable = false)
    var amount: BigDecimal? = BigDecimal.ZERO

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = LocalDateTime.now()

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: LocalDateTime? = null

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: OrderStatus? = null

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<OrderItem> = mutableListOf()

    @Column(name = "checkout_url")
    var checkoutUrl: String? = null

    constructor(items: List<OrderItem>) : this() {
        this.status = OrderStatus.CREATED
        this.orderUuid = UUID.randomUUID().toString()
        items.forEach { item -> item.order = this }
        this.items.addAll(items)
        amount = getTotalPrice()
    }

    fun getTotalPrice(): BigDecimal {
        return BigDecimal(items.sumOf {
            (it.price ?: BigDecimal.ZERO).multiply(BigDecimal(it.quantity!!)).toDouble()
        })
    }
}