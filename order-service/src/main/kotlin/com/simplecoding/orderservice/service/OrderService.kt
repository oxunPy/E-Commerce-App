package com.simplecoding.orderservice.service

import com.simplecoding.orderservice.domain.dto.CreateOrderRequestDto
import com.simplecoding.orderservice.domain.entity.Order

interface OrderService {
    fun create(request: CreateOrderRequestDto): Order
}