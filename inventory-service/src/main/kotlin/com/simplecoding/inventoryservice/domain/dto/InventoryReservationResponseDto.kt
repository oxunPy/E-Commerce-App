package com.simplecoding.inventoryservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.simplecoding.inventoryservice.domain.entity.InventoryReservation

data class InventoryReservationResponseDto (
    @JsonProperty("id")
    val id: Long?,

    @JsonProperty("inventory_id")
    val inventoryId: Long?,

    @JsonProperty("product_id")
    val productId: String?,

    @JsonProperty("quantity")
    val quantity: Int?,
) {
    companion object {
        fun fromInventoryReservation(reservation: InventoryReservation): InventoryReservationResponseDto {
            return InventoryReservationResponseDto(
                id = reservation.id,
                inventoryId = reservation.inventory?.id,
                productId = reservation.productId,
                quantity = reservation.quantity
            )
        }
    }
}