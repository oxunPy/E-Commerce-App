package com.simplecoding.inventoryservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import org.jetbrains.annotations.NotNull

data class CreateInventoryRequestDto(
    @JsonProperty("product_id")
    @NotNull("Ид продукта нелзя быт нулевым")
    val productId: String?,

    @JsonProperty("quantity")
    @NotNull("Количество нелзя быт нулевым")
    @Min(value = 1, message = "Минималная кол-во менще одного")
    val quantity: Int?
)