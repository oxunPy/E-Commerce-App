package com.simplecoding.paymentservice.usecase

import com.simplecoding.paymentservice.client.InventoryClient
import com.simplecoding.paymentservice.domain.dto.CompleteReservationRequestDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ReserveCompleteUseCase(
    private val inventoryClient: InventoryClient
) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    fun reserveComplete(request: CompleteReservationRequestDto) {
        inventoryClient.reserveComplete(request)
            .toFuture()
            .whenCompleteAsync { response, error ->
                if (error != null) {
                    log.error("Reserve complete error", error)
                }

                else if (response != null && response.success) {
                    log.debug("Reserve complete success")
                }
            }
    }
}