package com.simplecoding.paymentservice.messaging.producer

import com.simplecoding.paymentservice.domain.event.PaymentCompletedEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaPaymentCompletedProducer(
    private val reliableKafkaTemplate: KafkaTemplate<String, Any>
) {
    @Value($$"${app.topic.payment-completed-topic}")
    private lateinit var paymentCompletedTopic: String

    companion object {
        private val log = LoggerFactory.getLogger(KafkaPaymentCompletedProducer::class.java)
    }

    fun sendPaymentCompletedEvent(event: PaymentCompletedEvent) {
        val key = event.orderId.toString()
        val future = reliableKafkaTemplate.send(paymentCompletedTopic, key, event)
        future.whenComplete { response, error ->
            if (error != null) {
                log.error("Ошибка отправки события в кафка по orderId: {}", event.orderId, error)
                return@whenComplete
            }

            log.info(
                "Событие успешно отправлено в топик: {}, partition: {}, offset: {}",
                paymentCompletedTopic,
                response.recordMetadata.partition(),
                response.recordMetadata.offset()
            )
        }
    }
}