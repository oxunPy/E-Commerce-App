package com.simplecoding.orderservice.service

import com.simplecoding.orderservice.domain.dto.CancelOrderResponseDto
import com.simplecoding.orderservice.domain.dto.ConfirmPaymentRequestDto
import com.simplecoding.orderservice.domain.dto.ConfirmPaymentResponseDto
import com.simplecoding.orderservice.domain.dto.CreateOrderRequestDto
import com.simplecoding.orderservice.domain.entity.Order
import org.springframework.http.ResponseEntity

interface OrderService {
    fun create(request: CreateOrderRequestDto): Order

    fun confirm(request: ConfirmPaymentRequestDto): ConfirmPaymentResponseDto

    fun cancel(orderId: Long): CancelOrderResponseDto

    fun getOrderWithItems(id: Long): Order?
}