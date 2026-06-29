package com.simplecoding.productservice.repository

import com.simplecoding.productservice.domain.document.ProductDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface ProductSearchRepository: ElasticsearchRepository<ProductDocument, String> {
    fun findByNameContaining(name: String): List<ProductDocument>

    fun findByCategoryAndActiveTrue(category: String): List<ProductDocument>

}