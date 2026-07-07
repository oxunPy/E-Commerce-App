package com.simplecoding.orderservice.service

import com.simplecoding.orderservice.dictionary.OutboxStatus
import com.simplecoding.orderservice.domain.dto.CreateOrderRequestDto
import com.simplecoding.orderservice.domain.dto.OrderItemDto
import com.simplecoding.orderservice.domain.entity.Order
import com.simplecoding.orderservice.domain.entity.OrderItem
import com.simplecoding.orderservice.domain.event.OrderCreatedEvent
import com.simplecoding.orderservice.exception.OrderCreateException
import com.simplecoding.orderservice.metrics.annotation.BusinessMetric
import com.simplecoding.orderservice.outbox.OutboxEvent
import com.simplecoding.orderservice.repository.OrderRepository
import com.simplecoding.orderservice.repository.OutboxEventRepository
import io.micrometer.observation.annotation.Observed
import io.opentelemetry.api.trace.Span
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper
import java.time.LocalDateTime

@Service
class SagaOrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val objectMapper: ObjectMapper,
    private val outboxEventRepository: OutboxEventRepository
): SagaOrderService {

    companion object {
        private val log = LoggerFactory.getLogger(SagaOrderServiceImpl::class.java)
    }

    @BusinessMetric(
        value = "saga.order.create",
        tags = ["operation=create", "type=write"],
    )
    @Observed(name = "order.creation", contextualName = "Create order")
    @Transactional
    override fun createOrder(request: CreateOrderRequestDto): Order {
        log.debug("В метод OrderService.create получен запрос: {}", request)

        try {
            val items = request.items.map { item ->
                OrderItem(
                    productId = item.productId,
                    productName = item.productName,
                    quantity = item.quantity,
                    price = item.price,
                )
            }

            val order = Order(items)
            val savedOrder = orderRepository.saveAndFlush(order)
            log.debug("Заказ создан id: {}", savedOrder.id)

            val orderCreatedEvent = OrderCreatedEvent(
                orderId = savedOrder.id ?: 0L,
                context = MDC.getCopyOfContextMap(),
                items = items.map { OrderItemDto(it.id, savedOrder.id, it.productId, it.quantity ?: 0) },
                timestamp = LocalDateTime.now(),
            )

            // Сохраняем в outbox
            val traceId = Span.current().spanContext.traceId
            val spanId = Span.current().spanContext.spanId
            val traceparent = "00-$traceId-$spanId-01" // формат W3C

            val outboxEvent = OutboxEvent()
            outboxEvent.aggregationType = "Order"
            outboxEvent.aggregateId = savedOrder.id.toString()
            outboxEvent.payload = objectMapper.writeValueAsString(orderCreatedEvent)
            outboxEvent.eventType = "OrderCreatedEvent"
            outboxEvent.traceId = traceId
            outboxEvent.spanId = spanId
            outboxEvent.traceparent = traceparent
            outboxEvent.status = OutboxStatus.NEW
            outboxEventRepository.save(outboxEvent)

            log.debug("Отправлено инфо о заказе, id: {}", savedOrder.id)

            MDC.put("order_id", savedOrder.id.toString())
            MDC.put("total_amount", savedOrder.getTotalPrice().toString())
            MDC.put("status", savedOrder.status.toString())

            Span.current().setAttribute("order.id", savedOrder.id ?: 0)
            return savedOrder
        } catch (e: Exception) {
            val cause: Throwable = e.cause!!
            log.error("Ошибка при оформлении заказа {}", cause.message)
            throw OrderCreateException("Error: " + cause.message)
        } finally {
            MDC.remove("order_id")
            MDC.remove("total_amount")
            MDC.remove("status")
        }
    }
}