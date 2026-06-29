package com.simplecoding.productservice.service

import com.simplecoding.productservice.domain.dto.CreateProductRequestDto
import com.simplecoding.productservice.domain.entity.Product

interface ProductService {
    fun create(dto: CreateProductRequestDto): Product
}