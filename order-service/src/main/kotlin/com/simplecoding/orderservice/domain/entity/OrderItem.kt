package com.simplecoding.orderservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "order_items")
class OrderItem() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "product_id", nullable = false)
    var productId: String? = null

    @Column(name = "product_name", nullable = false)
    var productName: String? = null

    @Column(name = "quantity", columnDefinition = "integer DEFAULT 0", nullable = false)
    var quantity: Int? = 0

    @Column(name = "price", columnDefinition = "decimal DEFAULT 0", nullable = false)
    var price: BigDecimal? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order? = null

    constructor(
        productId: String?,
        productName: String?,
        quantity: Int?,
        price: BigDecimal?,
    ): this() {
        this.productId = productId
        this.productName = productName
        this.quantity = quantity
        this.price = price
    }
}