package com.simplecoding.productservice.controller

import com.simplecoding.productservice.domain.document.ProductDocument
import com.simplecoding.productservice.service.ProductSearchService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductSearchController(
    private val productSearchService: ProductSearchService
) {

    @GetMapping("/search")
    fun searchProducts(@RequestParam q: String) : ResponseEntity<List<ProductDocument>> {
        return ResponseEntity.ok(productSearchService.search(q))
    }

    @GetMapping("/category/{category}")
    fun byCategory(@PathVariable category: String): ResponseEntity<List<ProductDocument>> =
        ResponseEntity.ok(productSearchService.findByCategory(category))
}