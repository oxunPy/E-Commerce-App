package com.simplecoding.orderservice.listener

import com.simplecoding.orderservice.client.NotificationClient
import com.simplecoding.orderservice.domain.event.OrderCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class NotificationListener (
    private val notificationClient: NotificationClient
) {

    companion object {
        private val log = LoggerFactory.getLogger(NotificationListener::class.java)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleOrderCreated(event: OrderCreatedEvent) {
        log.info("Подготовлен событие к отправке: {}", event);

        try {
            notificationClient.notifyOrderCreated(event);
            log.debug("Уведомление успешно отправлено по id: {}", event.orderId);
        } catch (e: Exception) {
            log.error("Ошибка отправление события по созданию заказа id: {}", event.orderId, e);
        }
    }
}