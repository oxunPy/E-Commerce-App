package com.simplecoding.productservice.controller

import com.simplecoding.productservice.domain.dto.CreateProductRequestDto
import com.simplecoding.productservice.domain.dto.CreateProductResponseDto
import com.simplecoding.productservice.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService
) {

    @PostMapping("/create")
    fun createProduct(@RequestBody @Valid request: CreateProductRequestDto): ResponseEntity<CreateProductResponseDto> {
        val product = productService.create(request)

        return ResponseEntity.ok(CreateProductResponseDto(
            success = product.id != null,
            product = product
        ))
    }
}