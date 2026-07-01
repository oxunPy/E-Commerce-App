package com.simplecoding.paymentservice.controller

import com.simplecoding.paymentservice.domain.dto.CreatePaymentRequestDto
import com.simplecoding.paymentservice.domain.dto.CreatePaymentResponseDto
import com.simplecoding.paymentservice.service.PaymentService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val paymentService: PaymentService
) {

    @PostMapping("/process")
    fun createPayment(@RequestBody @Valid request: CreatePaymentRequestDto) : ResponseEntity<CreatePaymentResponseDto> {
        return ResponseEntity.ok(CreatePaymentResponseDto.createFromPayment(
            paymentService.create(request)
        ))
    }
}