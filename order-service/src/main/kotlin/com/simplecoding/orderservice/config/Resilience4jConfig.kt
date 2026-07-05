package com.simplecoding.orderservice.config

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.retry.RetryRegistry
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration

@Configuration
class Resilience4jConfig(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val retryRegistry: RetryRegistry
) {

    companion object {
        private val log = LoggerFactory.getLogger(Resilience4jConfig::class.java)
    }

    @PostConstruct
    fun registerEventListeners() {
        circuitBreakerRegistry.circuitBreaker("paymentService")
        circuitBreakerRegistry.circuitBreaker("inventoryService")
        circuitBreakerRegistry.circuitBreaker("notificationService")

        retryRegistry.retry("paymentService")
        retryRegistry.retry("inventoryService")
        retryRegistry.retry("notificationService")
    }

    fun CircuitBreakerRegistry.circuitBreaker(name: String) {
        this.circuitBreaker(name)
            .eventPublisher
            .onSuccess { event ->
                log.info("CB SUCCESS: время выполнения {} ms", event.elapsedDuration.toMillis())
            }
            .onError { event ->
                log.error(
                    "CB ERROR: время выполнения {} ms, ощибка: {}",
                    event.elapsedDuration.toMillis(),
                    event.throwable.message
                )
            }
            .onStateTransition { event ->
                log.warn(
                    "CB STATE TRANSITION: {} -> {}",
                    event.stateTransition.fromState,
                    event.stateTransition.toState
                )
            }
            .onCallNotPermitted { event ->
                log.warn("CB CALL NOT PERMITTED: вызов заблокирован (breaker открыт)")
            }
            .onIgnoredError { event ->
                log.debug(
                    "CB IGNORED ERROR: ошибка проигнорирована (не влияет на статистику): {}",
                    event.throwable.message
                )
            }
    }

    fun RetryRegistry.retry(name: String) {
        this.retry(name)
            .eventPublisher
            .onRetry { event ->
                log.debug("RETRY: попытка {} для {}", event.numberOfRetryAttempts, event.name)
            }
            .onSuccess { event ->
                log.info("SUCCESS: после {} попыток", event.numberOfRetryAttempts)
            }
            .onError { event ->
                log.error(
                    "ERROR: попытка {}, причина {}",
                    event.numberOfRetryAttempts,
                    event.lastThrowable.message
                )
            }
    }
}