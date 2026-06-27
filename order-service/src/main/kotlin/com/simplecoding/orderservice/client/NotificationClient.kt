package com.simplecoding.orderservice.client

import com.simplecoding.orderservice.domain.dto.NotificationRequestDto
import com.simplecoding.orderservice.domain.event.OrderCreatedEvent
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class NotificationClient(
    private val notificationWebClient: WebClient
) {

    companion object {
        private val log = LoggerFactory.getLogger(NotificationClient::class.java)
    }

    @Async
    fun notifyOrderCreated(event: OrderCreatedEvent) {
        val context = event.context
        MDC.setContextMap(context)


        try {
            log.info(
                "Отправка уведомления для заказа: {}, с traceID: {}, с суммой: {}",
                event.orderId,
                MDC.get("traceId"),
                MDC.get("total_amount")
            )

            notificationWebClient.post()
                .uri("/api/v1/notifications")
                .bodyValue(NotificationRequestDto(event.orderId, "CREATED"))
                .retrieve()
                .toBodilessEntity()
                .block()
        } finally {
            MDC.clear()
        }
    }
}