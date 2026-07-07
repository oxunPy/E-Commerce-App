package com.simplecoding.inventoryservice.messaging.consumer

import com.simplecoding.inventoryservice.domain.dto.CancelReservationRequestDto
import com.simplecoding.inventoryservice.domain.dto.CompleteReservationRequestDto
import com.simplecoding.inventoryservice.domain.event.PaymentCompletedEvent
import com.simplecoding.inventoryservice.domain.event.PaymentFailedEvent
import com.simplecoding.inventoryservice.service.InventoryService
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

@Component
class KafkaPaymentConsumer(
    private val objectMapper: ObjectMapper,
    private val inventoryService: InventoryService
) {

    companion object {
        private val log = LoggerFactory.getLogger(KafkaPaymentConsumer::class.java)
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
        topics = [$$"${app.topic.payment-completed-topic}"],
        groupId = $$"${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun handlePaymentCompleted(record: ConsumerRecord<String, ByteArray>, ack: Acknowledgment) {
        log.info("Принято сообщение из топика payment-completed-topic")

        val event = try {
            objectMapper.readValue(record.value(), PaymentCompletedEvent::class.java)
        } catch (e: Exception) {
            log.error("Ошибка десериализации события", e)
            ack.acknowledge()
            return
        }

        try {
            inventoryService.completeReserve(CompleteReservationRequestDto(orderId = event.orderId))
            ack.acknowledge()
            log.info("Оффсет для заказа с orderId: {} сдвинут", event.orderId)
        } catch(e: Exception) {
            log.error("Error processing PaymentCompletedEvent", e)
            throw RuntimeException("Error processing PaymentCompletedEvent", e)
        }
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
        topics = [$$"${app.topic.payment-failed-topic}"],
        groupId = $$"${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun handlePaymentFailed(record: ConsumerRecord<String, ByteArray>, ack: Acknowledgment) {
        log.info("Принято сообщение из топика payment-failed-topic")

        val event = try {
            objectMapper.readValue(record.value(), PaymentFailedEvent::class.java)
        } catch (e: Exception) {
            log.error("Ошибка десериализации события", e)
            ack.acknowledge()
            return
        }

        try {
            inventoryService.cancelReservation(CancelReservationRequestDto(orderId = event.orderId))
            ack.acknowledge()
            log.info("Оффсет для заказа с orderId: {} сдвинут", event.orderId)
        } catch(e: Exception) {
            log.error("Error processing PaymentFailedEvent", e)
            throw RuntimeException("Error processing PaymentFailedEvent", e)
        }
    }
}