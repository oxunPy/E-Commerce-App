package com.simplecoding.orderservice.service

import com.simplecoding.orderservice.client.PaymentClient
import com.simplecoding.orderservice.domain.dto.PaymentResponseDto
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

@Service
class PaymentServiceImpl(
    private val paymentClient: PaymentClient,
): PaymentService {

    override fun processPayment(orderId: Long, amount: BigDecimal): CompletableFuture<PaymentResponseDto?> {
        return paymentClient.processPayment(orderId, amount).toFuture()
    }
}