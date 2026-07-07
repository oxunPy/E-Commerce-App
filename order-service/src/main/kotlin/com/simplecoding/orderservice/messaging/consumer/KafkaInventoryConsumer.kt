package com.simplecoding.orderservice.messaging.consumer

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
class KafkaInventoryConsumer(
    private val objectMapper: ObjectMapper,
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

        try {
            val jsonNode = objectMapper.readTree(record.value())
            val eventId = jsonNode["id"].asLong()


        } catch (e: Exception) {

        }
    }


    @KafkaListener(
        topics = [$$"${app.topic.stock-reservation-failed-topic}"],
        groupId = $$"${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun handleStockReservationFailed(record: ConsumerRecord<String, ByteArray>, ack: Acknowledgment) {
        log.info("Принято сообщение из топика stock-reservation-failed-topic")


    }
}