package com.simplecoding.orderservice.service

import com.simplecoding.orderservice.domain.dto.CreateOrderRequestDto
import com.simplecoding.orderservice.domain.entity.Order
import com.simplecoding.orderservice.domain.entity.OrderItem
import com.simplecoding.orderservice.domain.event.OrderCreatedEvent
import com.simplecoding.orderservice.exception.NotFoundOrderException
import com.simplecoding.orderservice.exception.OrderCreatedException
import com.simplecoding.orderservice.metrics.annotation.BusinessMetric
import com.simplecoding.orderservice.repository.OrderItemRepository
import com.simplecoding.orderservice.repository.OrderRepository
import io.micrometer.observation.annotation.Observed
import io.opentelemetry.api.trace.Span
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean
import java.util.Random

@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : OrderService {
    private val failureMode = AtomicBoolean(true)
    private val random = Random()
    private val log = LoggerFactory.getLogger(OrderServiceImpl::class.java)

    @BusinessMetric(
        value = "orders.create",
        tags = ["operation=create", "type=write"]
    )
    @Observed(name = "order.creation", contextualName = "Create order")
    @Transactional
    override fun create(request: CreateOrderRequestDto): Order {
        log.debug("В метод OrderService.create получен запрос: {}", request)

        try {
            // симуляция ошибок
            runFail()

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

            MDC.put("order_id", savedOrder.id.toString());
            MDC.put("total_amount", savedOrder.getTotalPrice().toString());
            MDC.put("status", savedOrder.status.toString());

            applicationEventPublisher.publishEvent(
                OrderCreatedEvent(
                    orderId = savedOrder.id!!,
                    context = MDC.getCopyOfContextMap(),
                    timestamp = LocalDateTime.now()
                )
            )

            Span.current().setAttribute("order.id", savedOrder.id.toString());
            log.debug("Все успешно сохранено")
            return savedOrder
        } catch (e: Exception) {
            val cause = if (e.cause != null) e.cause else e
            log.error("Ошибка при создание заказа {}", cause?.message)
            throw OrderCreatedException("Order created error: ${cause?.message}")
        } finally {
            MDC.clear()
        }
    }

    @BusinessMetric(
        value = "orders.retrieve",
        tags = ["operation=get", "type=read"]
    )
    @Transactional(readOnly = true)
    override fun getOrderWithItems(id: Long): Order? {
        log.debug("В метод getOrderWithItems получен запрос поиска по id: {}", id)

        val order = orderRepository.findById(id).orElseThrow {
            NotFoundOrderException("закас не найден id: $id")
        }

        log.debug("Результат успешно найден")
        return order
    }

    private fun runFail() {
        if (failureMode.get()) {
            val randomInt = random.nextInt(100)

            log.debug("Выпало число: {}", randomInt);

            if (randomInt < 30) {
                log.error("Возникли проблемы с обработкой сохранения заказа");
                throw RuntimeException("Типа проблемы с обработкой заказа");
            }

            if (randomInt > 70) {
                log.warn("OrderService замедлился");
                Thread.sleep(200);
            }
        }
    }
}