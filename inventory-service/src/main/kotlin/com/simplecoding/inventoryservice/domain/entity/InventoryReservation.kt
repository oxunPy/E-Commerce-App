package com.simplecoding.inventoryservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "inventory_reservation")
class InventoryReservation() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "order_id", nullable = false)
    var orderId: Long? = null

    @Column(name = "product_id", nullable = false)
    var productId: String? = null

    var quantity: Int = 0

    @Column(name = "expires_at", nullable = false)
    var expiresAt: LocalDateTime? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    var inventory: Inventory? = null

    constructor(productId: String, quantity: Int, inventory: Inventory, orderId: Long): this() {
        this.productId = productId
        this.quantity = quantity
        this.inventory = inventory
        this.orderId = orderId
        this.expiresAt = LocalDateTime.now().plusMinutes(2);
    }

    enum class ReservationStatus {
        PENDING,
        COMPLETED,
        CANCELLED
    }
}