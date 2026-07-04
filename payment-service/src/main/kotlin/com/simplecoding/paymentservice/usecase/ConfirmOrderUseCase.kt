package com.simplecoding.paymentservice.usecase

import com.simplecoding.paymentservice.client.OrderClient
import com.simplecoding.paymentservice.domain.dto.ConfirmPaymentRequestDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ConfirmOrderUseCase(
    private val orderClient: OrderClient
) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    fun confirmOrder(request: ConfirmPaymentRequestDto) {
        orderClient.confirm(request)
            .toFuture()
            .whenCompleteAsync { response, error ->
                if (error != null) {
                    log.error(error.message)
                }

                else if (response != null && response.success) {
                    log.debug("Successfully confirmed order")
                }
            }
    }
}