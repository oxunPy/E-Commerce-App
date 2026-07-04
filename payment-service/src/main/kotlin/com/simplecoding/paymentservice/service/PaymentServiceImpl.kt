package com.simplecoding.paymentservice.service

import com.simplecoding.paymentservice.domain.PaymentStatus
import com.simplecoding.paymentservice.domain.dto.CheckoutPaymentRequestDto
import com.simplecoding.paymentservice.domain.dto.CheckoutPaymentResponseDto
import com.simplecoding.paymentservice.domain.dto.CompleteReservationRequestDto
import com.simplecoding.paymentservice.domain.dto.ConfirmPaymentRequestDto
import com.simplecoding.paymentservice.domain.dto.CreatePaymentRequestDto
import com.simplecoding.paymentservice.domain.entity.Payment
import com.simplecoding.paymentservice.exception.CheckoutPaymentException
import com.simplecoding.paymentservice.exception.PaymentCreateException
import com.simplecoding.paymentservice.repository.PaymentRepository
import com.simplecoding.paymentservice.usecase.ConfirmOrderUseCase
import com.simplecoding.paymentservice.usecase.ReserveCompleteUseCase
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Random
import java.util.concurrent.atomic.AtomicBoolean

@Service
class PaymentServiceImpl(
    private val paymentRepository: PaymentRepository,
    private val confirmOrderUseCase: ConfirmOrderUseCase,
    private val reserveCompleteUseCase: ReserveCompleteUseCase
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

    override fun checkout(request: CheckoutPaymentRequestDto): CheckoutPaymentResponseDto {
        try {
            var payment = paymentRepository.findById(request.paymentId!!).orElseThrow {
                RuntimeException("payment not found")
            }

            if (payment.status != PaymentStatus.PENDING) {
                throw RuntimeException("payment not pending status")
            }

            payment.status = PaymentStatus.SUCCEEDED
            payment = paymentRepository.save(payment)

            // reserve complete
            reserveCompleteUseCase.reserveComplete(CompleteReservationRequestDto(
                orderId = payment.orderId
            ))

            // order confirm
            confirmOrderUseCase.confirmOrder(ConfirmPaymentRequestDto(
                orderId = payment.orderId,
                paymentId = payment.id,
                amount = payment.amount!!,
                status = payment.status
            ))

            return CheckoutPaymentResponseDto(
                paymentId = payment.id,
                orderId = payment.orderId,
                amount = payment.amount,
                success = true,
                message = "Payment successful",
            )
        } catch (e: Exception) {
            val cause = e.cause ?: e
            log.error("Payment checkout error", cause)
            throw CheckoutPaymentException("Payment checkout error: ${cause.message}")
        }
    }

    private fun runFail() {
        if (failMode.get()) {
            val randomInt = random.nextInt(100)
            log.debug("Выпало число (payment-service): $randomInt")

            if (randomInt < 30) {
                log.error("Возникли проблемы с обработкой сохранения платежа")
                throw RuntimeException("Типа проблемы с обработкой платежа")
            }

            if (randomInt > 70) {
                log.warn("PaymentService замедлился")
                Thread.sleep(200)
            }
        }
    }
}