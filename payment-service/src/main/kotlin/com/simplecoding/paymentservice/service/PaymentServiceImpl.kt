package com.simplecoding.paymentservice.service

import com.simplecoding.paymentservice.domain.dto.CreatePaymentRequestDto
import com.simplecoding.paymentservice.domain.entity.Payment
import com.simplecoding.paymentservice.exception.PaymentCreateException
import com.simplecoding.paymentservice.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Random
import java.util.concurrent.atomic.AtomicBoolean

@Service
class PaymentServiceImpl(
    private val paymentRepository: PaymentRepository
) : PaymentService {
    companion object {
        private val log = LoggerFactory.getLogger(PaymentServiceImpl::class.java)
    }

    private val random = Random()
    private val failMode = AtomicBoolean(true)

    override fun create(request: CreatePaymentRequestDto): Payment {
        log.debug("В метод PaymentService.create получен запрос: {}", request)
        try {
            runFail()

            val payment = Payment(request.orderId!!, request.amount)
            paymentRepository.save(payment)
            return payment
        } catch (e: Exception) {
            val cause = e.cause ?: e
            log.error("Ошибка при создание нового платежа: {}", cause.message)
            throw PaymentCreateException("Payment create error: ${cause.message}")
        }
    }

    private fun runFail() {
        if (failMode.get()) {
            val randomInt = random.nextInt(100)
            log.debug("Выпало число (payment-service): $randomInt")

            if (randomInt < 30) {
                log.error("Возникли проблемы с обработкой сохранения платежа");
                throw RuntimeException("Типа проблемы с обработкой платежа");
            }

            if (randomInt > 70) {
                log.warn("PaymentService замедлился");
                Thread.sleep(200);
            }
        }
    }
}