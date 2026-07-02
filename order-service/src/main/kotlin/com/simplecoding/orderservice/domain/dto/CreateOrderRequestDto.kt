package com.simplecoding.orderservice.domain.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal


data class CreateOrderRequestDto(
    @NotEmpty val items: List<OrderItemRequest>
) {
    data class OrderItemRequest (
        @NotBlank(message = "productId не должен быть пустим")
        var productId: String?,

        @NotBlank(message = "productName не должен быть пустим")
        val productName: String,

        @Min(value = 1, message = "количество не может быт меньше одного")
        val quantity: Int,

        @DecimalMin(value = "0.01", message = "цена должен быть больше одного")
        val price: BigDecimal,
    ) {

    }
}
