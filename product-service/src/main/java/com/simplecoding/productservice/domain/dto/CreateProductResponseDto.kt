package com.simplecoding.productservice.domain.dto

import com.simplecoding.productservice.domain.entity.Product

data class CreateProductResponseDto (
    val success: Boolean,
    val product: Product?
)