package com.simplecoding.orderservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CheckoutOrderResponseDto(
    @JsonProperty("order_id")
    val orderId: Long,

    @JsonProperty("checkout_url")
    val checkoutUrl: String?
)