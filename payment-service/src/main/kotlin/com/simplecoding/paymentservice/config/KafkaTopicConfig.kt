package com.simplecoding.paymentservice.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig {
    @Value($$"${app.topic.payment-completed-topic}")
    private lateinit var paymentCompletedTopic: String

    @Value($$"${app.topic.payment-failed-topic}")
    private lateinit var paymentFailedTopic: String

    @Bean
    fun paymentCompletedTopic(): NewTopic {
        return TopicBuilder.name(paymentCompletedTopic)
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun paymentFailedTopic(): NewTopic {
        return TopicBuilder.name(paymentFailedTopic)
            .partitions(3)
            .replicas(1)
            .build()
    }
}