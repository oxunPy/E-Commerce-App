package com.simplecoding.paymentservice.config

import io.micrometer.observation.ObservationRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Value($$"${url.order-service}")
    private lateinit var orderServiceUrl: String

    @Value($$"${url.inventory-service}")
    private lateinit var inventoryServiceUrl: String

    @Bean
    fun webClientBuilder(observationRegistry: ObservationRegistry) =
        WebClient.builder()
            .observationRegistry(observationRegistry)

    @Bean
    fun orderWebClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl(orderServiceUrl).build()
    }

    @Bean
    fun inventoryWebClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl(inventoryServiceUrl).build()
    }
}