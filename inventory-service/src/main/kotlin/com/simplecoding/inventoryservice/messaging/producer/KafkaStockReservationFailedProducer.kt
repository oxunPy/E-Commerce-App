package com.simplecoding.inventoryservice.messaging.producer

import com.simplecoding.inventoryservice.domain.event.StockReservationFailedEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaStockReservationFailedProducer(
    private val reliableKafkaTemplate: KafkaTemplate<String, Any>
) {
    @Value($$"${app.topic.stock-reservation-failed-topic}")
    private lateinit var stockReservationFailedTopic: String

    companion object {
        private val log = LoggerFactory.getLogger(KafkaStockReservationFailedProducer::class.java)
    }

    fun sendStockReservationFailedEvent(event: StockReservationFailedEvent) {
        val key = event.orderId.toString()
        val future = reliableKafkaTemplate.send(stockReservationFailedTopic, key, event)
        future.whenComplete { response, error ->
            if (error != null) {
                log.error("Ошибка отправки события в кафка по orderId: {}", event.orderId, error)
                return@whenComplete
            }

            log.info(
                "Событие успешно отправлено в топик: {}, partition: {}, offset: {}",
                stockReservationFailedTopic,
                response.recordMetadata.partition(),
                response.recordMetadata.offset()
            )
        }
    }
}