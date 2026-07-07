package com.simplecoding.paymentservice.messaging.producer

import com.simplecoding.paymentservice.domain.event.PaymentFailedEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaPaymentFailedProducer(
    private val reliableKafkaTemplate: KafkaTemplate<String, Any>
) {
    @Value($$"${app.topic.payment-failed-topic}")
    private lateinit var paymentFailedEventTopic: String

    companion object {
        private val log = LoggerFactory.getLogger(KafkaPaymentFailedProducer::class.java)
    }

    fun sendPaymentFailedEvent(event: PaymentFailedEvent) {
        val key = event.orderId.toString()
        val future = reliableKafkaTemplate.send(paymentFailedEventTopic, key, event)
        future.whenComplete { response, error ->
            if (error != null) {
                log.error("Ошибка отправки события в кафка по orderId: {}", event.orderId, error)
                return@whenComplete
            }

            log.info(
                "Событие успешно отправлено в топик: {}, partition: {}, offset: {}",
                paymentFailedEventTopic,
                response.recordMetadata.partition(),
                response.recordMetadata.offset()
            )
        }
    }
}