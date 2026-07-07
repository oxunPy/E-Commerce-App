package com.simplecoding.orderservice.outbox

import com.simplecoding.orderservice.dictionary.OutboxStatus
import com.simplecoding.orderservice.domain.event.OrderCreatedEvent
import com.simplecoding.orderservice.messaging.producer.KafkaOrderCreatedEventProducer
import com.simplecoding.orderservice.repository.OutboxEventRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper
import java.time.ZonedDateTime

@Component
class OutboxPoller(
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper,
    private val kafkaOrderCreatedEventProducer: KafkaOrderCreatedEventProducer
) {

    companion object {
        private val log = LoggerFactory.getLogger(OutboxPoller::class.java)
    }

    @Scheduled(fixedDelay = 1 * 60 * 1000, initialDelay = 5000)
    @Transactional
    fun poll() {
        val events = outboxEventRepository.claimNewEvents(100)
        if (events.isEmpty()) return

        log.info("Found {} events to publish", events.size)

        for (event in events) {
            try {
                val orderCreatedEvent = objectMapper.readValue(
                    event.payload?.toByteArray(),
                    OrderCreatedEvent::class.java
                )

                kafkaOrderCreatedEventProducer.sendOrderCreatedEvent(
                    orderCreatedEvent
                )

                event.status = OutboxStatus.PUBLISHED
                event.processedAt = ZonedDateTime.now()
                outboxEventRepository.save(event)

                log.info("Event for order {} published to Kafka", orderCreatedEvent.orderId)
            } catch (e: Exception) {
                log.error("Failed to publish event id={}", event.id, e)
            }
        }
    }
}