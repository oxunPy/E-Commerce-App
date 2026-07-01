package com.simplecoding.inventoryservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.simplecoding.inventoryservice.domain.entity.Inventory

data class CreateInventoryResponseDto(
    @JsonProperty("inventory_id")
    val inventoryId: Long?,

    @JsonProperty("product_id")
    val productId: String?,

    @JsonProperty("quantity")
    val quantity: Int
) {
    companion object {
        fun fromInventory(inventory: Inventory): CreateInventoryResponseDto {
            return CreateInventoryResponseDto(
                inventoryId = inventory.id,
                productId = inventory.productId,
                quantity = inventory.quantity
            )
        }
    }
}