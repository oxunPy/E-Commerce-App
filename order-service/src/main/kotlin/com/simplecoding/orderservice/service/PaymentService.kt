package com.simplecoding.orderservice.service

import com.simplecoding.orderservice.domain.dto.PaymentResponseDto
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

interface PaymentService {
    fun processPayment(orderId: Long, amount: BigDecimal): CompletableFuture<PaymentResponseDto?>
}