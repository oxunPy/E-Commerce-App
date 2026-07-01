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
@Table(name = "inventory_history")
class InventoryHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "product_id", nullable = false)
    var productId: String? = null

    @Column(name = "quantity", nullable = false)
    var quantity: Int = 0

    @Column(name = "transaction_type", nullable = false)
    var transactionType: TransactionType? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    var inventory: Inventory? = null

    enum class TransactionType {
        STOCK_IN,
        SOLD,
        REFUND,
        WASTE
    }
}