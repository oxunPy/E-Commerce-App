package com.simplecoding.inventoryservice.messaging.consumer

import com.simplecoding.inventoryservice.domain.dto.InventoryReservationRequestDto
import com.simplecoding.inventoryservice.domain.event.OrderCreatedEvent
import com.simplecoding.inventoryservice.domain.event.StockReservationFailedEvent
import com.simplecoding.inventoryservice.domain.event.StockReservedEvent
import com.simplecoding.inventoryservice.messaging.producer.KafkaStockReservationFailedProducer
import com.simplecoding.inventoryservice.messaging.producer.KafkaStockReservedEventProducer
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
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper
import java.math.BigDecimal

@Component
class KafkaOrderConsumer(
    private val objectMapper: ObjectMapper,
    private val inventoryService: InventoryService,
    private val kafkaStockReservedEventProducer: KafkaStockReservedEventProducer,
    private val kafkaSTockReservationFailedProducer: KafkaStockReservationFailedProducer,
) {

    companion object {
        private val log = LoggerFactory.getLogger(KafkaOrderConsumer::class.java)
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
        topics = [$$"${app.topic.order-created-topic}"],
        groupId = $$"${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    fun handleOrderCreated(record: ConsumerRecord<String, ByteArray>, ack: Acknowledgment) {
        log.info("Принято сообщение из топика order-created-topic")

        val event = try {
            objectMapper.readValue(record.value(), OrderCreatedEvent::class.java)
        } catch (e: Exception) {
            log.error("Ошибка десериализации события", e)
            ack.acknowledge()
            return
        }

        try {
            event.items.forEach { item ->
                inventoryService.reserve(
                    InventoryReservationRequestDto(
                        item.productId,
                        item.orderId,
                        item.quantity
                    )
                )
            }

            kafkaStockReservedEventProducer.sendStockReservedEvent(StockReservedEvent(
                orderId = event.orderId,
                amount = event.context["total_amount"]?.toBigDecimal() ?: BigDecimal.ZERO,
                products = event.items.map { it.productId }.toList(),
            ))

            ack.acknowledge()
            log.info("Оффсет для заказа с orderId: {} сдвинут", event.orderId)
        } catch(e: Exception) {
            log.error("Error processing StockReservedEvent", e)
            kafkaSTockReservationFailedProducer.sendStockReservationFailedEvent(StockReservationFailedEvent(
                orderId = event.orderId,
                reason = e.message
            ))
            throw RuntimeException("Error processing StockReservedEvent", e)
        }
    }
}