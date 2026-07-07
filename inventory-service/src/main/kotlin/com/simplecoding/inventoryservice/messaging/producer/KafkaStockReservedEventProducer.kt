package com.simplecoding.inventoryservice.messaging.producer

import com.simplecoding.inventoryservice.domain.event.StockReservedEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaStockReservedEventProducer(
    private val reliableKafkaTemplate: KafkaTemplate<String, Any>
) {
    @Value($$"${app.topic.stock-reserved-topic}")
    private lateinit var stockReservedTopic: String


    companion object {
        private val log = LoggerFactory.getLogger(KafkaStockReservedEventProducer::class.java)
    }

    fun sendStockReservedEvent(event: StockReservedEvent) {
        val key = event.orderId.toString()
        val future = reliableKafkaTemplate.send(stockReservedTopic, key, event)
        future.whenComplete { response, error ->
            if (error != null) {
                log.error("Ошибка отправки события в кафка по orderId: {}", event.orderId, error)
                return@whenComplete
            }

            log.info(
                "Событие успешно отправлено в топик: {}, partition: {}, offset: {}",
                stockReservedTopic,
                response.recordMetadata.partition(),
                response.recordMetadata.offset()
            )
        }
    }
}