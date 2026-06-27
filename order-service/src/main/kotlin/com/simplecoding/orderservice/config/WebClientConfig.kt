package com.simplecoding.orderservice.config

import io.micrometer.observation.ObservationRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Value($$"${url.notification-service}")
    private lateinit var notificationServiceUrl: String

    @Value($$"${url.payment-service}")
    private lateinit var paymentServiceUrl: String


    @Bean
    fun webClientBuilder(observationRegistry: ObservationRegistry) =
        WebClient.builder()
            .observationRegistry(observationRegistry)

    @Bean
    fun notificationWebClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl(notificationServiceUrl).build()
    }

    @Bean
    fun paymentWebClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl(paymentServiceUrl).build()
    }
}