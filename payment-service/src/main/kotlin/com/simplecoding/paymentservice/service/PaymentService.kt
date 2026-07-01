package com.simplecoding.paymentservice.service

import com.simplecoding.paymentservice.domain.dto.CreatePaymentRequestDto
import com.simplecoding.paymentservice.domain.entity.Payment

interface PaymentService {
    fun create(request: CreatePaymentRequestDto): Payment
}