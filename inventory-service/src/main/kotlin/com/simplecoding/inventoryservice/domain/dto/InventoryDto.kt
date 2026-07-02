package com.simplecoding.inventoryservice.domain.dto

import com.simplecoding.inventoryservice.domain.entity.Inventory
import java.time.LocalDateTime

data class InventoryDto(
    val id: Long?,

    val productId: String??,

    val quantity: Int = 0,

    val createdAt: LocalDateTime
) {
    companion object {
        fun fromInventory(inventory: Inventory): InventoryDto {
            return InventoryDto(
                id = inventory.id,
                productId = inventory.productId,
                quantity = inventory.quantity,
                createdAt = inventory.createdAt
            )
        }
    }
}