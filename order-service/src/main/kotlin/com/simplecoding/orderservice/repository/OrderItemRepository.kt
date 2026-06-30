package com.simplecoding.orderservice.repository

import com.simplecoding.orderservice.domain.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItem, Long>