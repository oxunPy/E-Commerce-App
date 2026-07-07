package com.simplecoding.orderservice.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig {
    @Value($$"${app.topic.order-created-topic}")
    private lateinit var orderCreatedTopic: String

    @Bean
    fun orderCreatedTopic(): NewTopic {
        return TopicBuilder.name(orderCreatedTopic)
            .partitions(3)
            .replicas(1)
            .build()
    }
}