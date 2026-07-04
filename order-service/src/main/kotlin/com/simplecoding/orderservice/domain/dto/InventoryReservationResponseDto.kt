package com.simplecoding.orderservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class InventoryReservationResponseDto(
    @JsonProperty("id")
    val id: Long?,

    @JsonProperty("inventory_id")
    val inventoryId: Long?,

    @JsonProperty("product_id")
    val productId: String?,

    @JsonProperty("quantity")
    val quantity: Int?,
)