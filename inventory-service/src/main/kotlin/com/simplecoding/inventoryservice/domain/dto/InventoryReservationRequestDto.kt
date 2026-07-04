package com.simplecoding.inventoryservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class InventoryReservationRequestDto(
    @JsonProperty("product_id")
    @NotBlank(message = "Ид товара не может быт пустим")
    val productId: String?,

    @JsonProperty("order_id")
    @NotNull(message = "Ид заказа не может быт нулевым")
    var orderId: Long?,

    @JsonProperty("quantity")
    @Min(1, message = "минимальная задержка товара менще одного")
    val quantity: Int
)