package com.simplecoding.paymentservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.simplecoding.paymentservice.domain.PaymentStatus
import com.simplecoding.paymentservice.domain.entity.Payment
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreatePaymentResponseDto(
    @JsonProperty("payment_id")
    val paymentId: Long?,

    @JsonProperty("order_id")
    val orderId: Long?,

    @JsonProperty("status")
    val status: PaymentStatus,

    @JsonProperty("created_at")
    val createdAt: LocalDateTime,

    @JsonProperty("amount")
    val amount: BigDecimal?,

    @JsonProperty("checkout_url")
    val checkoutUrl: String?,
) {
    companion object {
        fun createFromPayment(payment: Payment): CreatePaymentResponseDto {
            return CreatePaymentResponseDto(
                payment.id,
                orderId = payment.orderId,
                status = payment.status,
                createdAt = payment.createdAt,
                amount = payment.amount,
                checkoutUrl = "/api/v1/payments/checkout/${payment.id}",
            )
        }
    }
}
