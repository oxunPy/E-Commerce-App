package com.simplecoding.paymentservice.service

import com.simplecoding.paymentservice.domain.dto.CreatePaymentRequestDto
import com.simplecoding.paymentservice.domain.entity.Payment
import com.simplecoding.paymentservice.exception.PaymentCreateException
import com.simplecoding.paymentservice.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SagaPaymentServiceImpl(
    private val paymentRepository: PaymentRepository,

): SagaPaymentService {

    companion object {
        private val log = LoggerFactory.getLogger(SagaPaymentServiceImpl::class.java)
    }

    override fun create(request: CreatePaymentRequestDto): Payment {
        log.info("В метод SagaPaymentService.create получен запрос: {}", request)

        try {
            if (request.orderId == null) {
                throw NullPointerException("orderId is null")
            }

            val payment = Payment(request.orderId!!, request.amount)
            paymentRepository.save(payment)
            return payment
        } catch (e: Exception) {
            val cause = e.cause ?: e
            log.error("Ошибка при создание нового платежа: {}", cause.message)
            throw PaymentCreateException("Payment create error: ${cause.message}")
        }
    }
}