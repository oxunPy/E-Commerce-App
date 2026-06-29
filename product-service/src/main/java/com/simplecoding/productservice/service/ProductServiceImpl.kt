package com.simplecoding.productservice.service

import com.simplecoding.productservice.domain.dto.CreateProductRequestDto
import com.simplecoding.productservice.domain.entity.Product
import com.simplecoding.productservice.repository.ProductRepository
import com.simplecoding.productservice.repository.ProductSearchRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val productSearchRepository: ProductSearchRepository
) : ProductService {

    @Transactional
    override fun create(dto: CreateProductRequestDto): Product {
        val product = Product(
            name = dto.name.orEmpty(),
            description = dto.description,
            price = dto.price?.toDouble() ?: 0.0,
            category = dto.category,
        )

        productRepository.save(product)
        productSearchRepository.save(product.toDocument())

        return product
    }
}