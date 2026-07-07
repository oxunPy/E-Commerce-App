package com.simplecoding.inventoryservice.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig {
    @Value($$"${app.topic.stock-reserved-topic}")
    private lateinit var stockReservedTopic: String

    @Value($$"${app.topic.stock-reservation-failed-topic}")
    private lateinit var stockReservationFailedTopic: String

    @Bean
    fun stockReservedTopic(): NewTopic {
        return TopicBuilder.name(stockReservedTopic)
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun stockReservationFailedTopic(): NewTopic {
        return TopicBuilder.name(stockReservationFailedTopic)
            .partitions(3)
            .replicas(1)
            .build()
    }
}