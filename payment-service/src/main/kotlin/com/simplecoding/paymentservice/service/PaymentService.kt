package com.simplecoding.paymentservice.service

import com.simplecoding.paymentservice.domain.dto.CheckoutPaymentRequestDto
import com.simplecoding.paymentservice.domain.dto.CheckoutPaymentResponseDto
import com.simplecoding.paymentservice.domain.dto.CreatePaymentRequestDto
import com.simplecoding.paymentservice.domain.entity.Payment

interface PaymentService {
    fun create(request: CreatePaymentRequestDto): Payment

    fun checkout(request: CheckoutPaymentRequestDto): CheckoutPaymentResponseDto
}