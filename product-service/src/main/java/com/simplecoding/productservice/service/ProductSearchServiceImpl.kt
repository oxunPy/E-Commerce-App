package com.simplecoding.productservice.service

import com.simplecoding.productservice.domain.document.ProductDocument
import com.simplecoding.productservice.repository.ProductSearchRepository
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.stereotype.Service

@Service
class ProductSearchServiceImpl(
    private val productSearchRepository: ProductSearchRepository,
    private val elasticSearchOperations: ElasticsearchOperations
) : ProductSearchService {

    override fun search(query: String): List<ProductDocument> {
        val criteria = Criteria("name").matches(query)
            .or(Criteria("description").matches(query))

        val searchQuery = CriteriaQuery(criteria)
        return elasticSearchOperations
            .search(searchQuery, ProductDocument::class.java)
            .map { it.content }
            .toList()
    }

    override fun findByCategory(category: String): List<ProductDocument> {
        return productSearchRepository.findByCategoryAndActiveTrue(category)
    }
}