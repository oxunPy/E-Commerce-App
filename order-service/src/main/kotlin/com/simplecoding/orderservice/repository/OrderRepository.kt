package com.simplecoding.orderservice.repository

import com.simplecoding.orderservice.domain.entity.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
}