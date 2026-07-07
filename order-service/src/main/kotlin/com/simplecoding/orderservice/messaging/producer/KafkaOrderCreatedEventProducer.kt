package com.simplecoding.orderservice.messaging.producer

import com.simplecoding.orderservice.domain.event.OrderCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaOrderCreatedEventProducer(
    private val reliableKafkaTemplate: KafkaTemplate<String, Any>,
) {
    @Value($$"${app.topic.order-created-topic}")
    private lateinit var orderCreatedTopic: String

    companion object {
        private val log = LoggerFactory.getLogger(KafkaOrderCreatedEventProducer::class.java)
    }


    fun sendOrderCreatedEvent(event: OrderCreatedEvent) {
        val key = event.orderId.toString()
        val future = reliableKafkaTemplate.send(orderCreatedTopic, key, event)
        future.whenComplete { response, error ->
            if (error != null) {
                log.error("Ошибка отправки события в кафка по orderId: {}", event.orderId, error)
                return@whenComplete
            }

            log.info(
                "Событие успешно отправлено в топик: {}, partition: {}, offset: {}",
                orderCreatedTopic,
                response!!.recordMetadata.partition(),
                response.recordMetadata.offset()
            )
        }
    }
}