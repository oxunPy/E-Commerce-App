package com.simplecoding.inventoryservice.domain.dto

import com.simplecoding.inventoryservice.domain.entity.InventoryReservation

data class InventoryReservationDto (
    val id: Long?,
    val inventoryId: Long?,
    val productId: String?,
    val quantity: Int?,
) {
    companion object {
        fun fromInventoryReservation(reservation: InventoryReservation): InventoryReservationDto {
            return InventoryReservationDto(
                id = reservation.id,
                inventoryId = reservation.inventory?.id,
                productId = reservation.productId,
                quantity = reservation.quantity
            )
        }
    }
}