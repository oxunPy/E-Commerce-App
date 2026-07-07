package com.simplecoding.paymentservice.messaging.consumer

import com.simplecoding.paymentservice.domain.dto.CreatePaymentRequestDto
import com.simplecoding.paymentservice.domain.event.PaymentCompletedEvent
import com.simplecoding.paymentservice.domain.event.PaymentFailedEvent
import com.simplecoding.paymentservice.domain.event.StockReservedEvent
import com.simplecoding.paymentservice.messaging.producer.KafkaPaymentCompletedProducer
import com.simplecoding.paymentservice.messaging.producer.KafkaPaymentFailedProducer
import com.simplecoding.paymentservice.service.SagaPaymentService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.BackOff
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.math.BigDecimal

@Component
class KafkaInventoryConsumer(
    private val objectMapper: ObjectMapper,
    private val sagaPaymentService: SagaPaymentService,
    private val kafkaPaymentCompletedProducer: KafkaPaymentCompletedProducer,
    private val kafkaPaymentFailedProducer: KafkaPaymentFailedProducer
) {

    companion object {
        private val log = LoggerFactory.getLogger(KafkaInventoryConsumer::class.java)
    }

    @RetryableTopic(
        attempts = "3",
        backOff = BackOff(
            delay = 1000,
            maxDelay = 10_000,
            multiplier = 2.0
        ),
        timeout = "60000",
        retryTopicSuffix = "-retry",
        dltTopicSuffix = ".DLT",
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.SINGLE_TOPIC,
        exclude = [IllegalArgumentException::class, NullPointerException::class],
        traversingCauses = "true",
        dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR,
        autoCreateTopics = "true",
        numPartitions = "1",
        replicationFactor = "1",
        listenerContainerFactory = "kafkaListenerContainerFactory",
        concurrency = "3",
        autoStartDltHandler = "true"
    )
    @KafkaListener(
        topics = [$$"${app.topic.stock-reserved-topic}"],
        groupId = $$"${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun handleStockReserved(record: ConsumerRecord<String, ByteArray>, ack: Acknowledgment) {
        log.info("Принято сообщение из топика stock-reserved-topic")

        val event = try {
            objectMapper.readValue(record.value(), StockReservedEvent::class.java)
        } catch (e: Exception) {
            log.error("Ошибка десериализации события", e)
            ack.acknowledge()
            return
        }

        try {
            if (event.transactionId == null) {
                throw NullPointerException("transactionId is null")
            }

            val payment = sagaPaymentService.create(CreatePaymentRequestDto(event.orderId, event.amount))
            kafkaPaymentCompletedProducer.sendPaymentCompletedEvent(PaymentCompletedEvent(
                orderId = event.orderId,
                transactionId = payment.id,
                amount = payment.amount ?: BigDecimal.ZERO,
                status = payment.status
            ))

            ack.acknowledge()
            log.info("Оффсет для заказа с orderId: {} сдвинут", event.orderId)
        } catch(e: Exception) {
            log.error("Error processing StockReservedEvent", e)
            kafkaPaymentFailedProducer.sendPaymentFailedEvent(PaymentFailedEvent(
                orderId = event.orderId,
                reason = e.message
            ))
            throw RuntimeException("Error processing StockReservedEvent", e)
        }
    }
}