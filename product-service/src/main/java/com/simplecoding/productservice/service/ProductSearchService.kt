package com.simplecoding.productservice.service

import com.simplecoding.productservice.domain.document.ProductDocument

interface ProductSearchService {
    fun search(query: String): List<ProductDocument>

    fun findByCategory(category: String): List<ProductDocument>
}