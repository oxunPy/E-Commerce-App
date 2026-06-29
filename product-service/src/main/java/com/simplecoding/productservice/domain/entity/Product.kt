package com.simplecoding.productservice.domain.entity

import com.simplecoding.productservice.domain.document.ProductDocument
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    val name: String,
    val description: String?,
    val price: Double,
    val category: String?,
    val active: Boolean = true,
) {

    fun toDocument() = ProductDocument(
        id = id ?: UUID.randomUUID().toString(),
        name = name,
        description = description,
        price = price,
        category = category,
        active = active
    )
}
