package com.simplecoding.productservice.repository

import com.simplecoding.productservice.domain.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, String>