package com.simplecoding.inventoryservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.LocalDateTime

@Entity
@Table(name = "inventory", indexes = [Index(name = "idx_product_id", columnList = "product_id")])
class Inventory() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "product_id", nullable = false)
    var productId: String? = null

    @Column(name = "quantity", nullable = false)
    var quantity: Int = 0

    @Version
    var version: Long? = null

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor(productId: String, quantity: Int): this() {
        this.productId = productId
        this.quantity = quantity
        version = 0
    }
}