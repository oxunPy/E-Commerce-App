package com.simplecoding.productservice.domain.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class CreateProductRequestDto (
    @NotBlank(message = "Имя продукта не может быт пустым")
    val name: String?,

    val description: String?,

    val category: String?,

    @DecimalMin(value = "0.001", message = "цена товара менще")
    val price: BigDecimal?,
)