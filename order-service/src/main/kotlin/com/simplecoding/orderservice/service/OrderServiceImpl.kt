package com.simplecoding.orderservice.service

import com.simplecoding.orderservice.domain.OrderStatus
import com.simplecoding.orderservice.domain.PaymentStatus
import com.simplecoding.orderservice.domain.dto.CancelOrderResponseDto
import com.simplecoding.orderservice.domain.dto.ConfirmPaymentRequestDto
import com.simplecoding.orderservice.domain.dto.ConfirmPaymentResponseDto
import com.simplecoding.orderservice.domain.dto.CreateOrderRequestDto
import com.simplecoding.orderservice.domain.entity.Order
import com.simplecoding.orderservice.domain.entity.OrderItem
import com.simplecoding.orderservice.domain.event.OrderCreatedEvent
import com.simplecoding.orderservice.exception.NotFoundOrderException
import com.simplecoding.orderservice.exception.OrderCancelException
import com.simplecoding.orderservice.exception.OrderConfirmException
import com.simplecoding.orderservice.exception.OrderCreateException
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
    private val inventoryService: InventoryService,
    private val paymentService: PaymentService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    service: InventoryService,
    paymentService1: PaymentService
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

            MDC.put("order_id", savedOrder.id.toString())
            MDC.put("total_amount", savedOrder.getTotalPrice().toString())
            MDC.put("status", savedOrder.status.toString())

            applicationEventPublisher.publishEvent(
                OrderCreatedEvent(
                    orderId = savedOrder.id!!,
                    context = MDC.getCopyOfContextMap(),
                    timestamp = LocalDateTime.now()
                )
            )

            // reserving item products
            order.items.forEach { item ->
                inventoryService.reserveProduct(item.productId!!, order.id!!, item.quantity!!)
                    .whenCompleteAsync { value, error ->
                        if (error != null) {
                            throw RuntimeException("failed to reserve product", error)
                        } else if (value != null) {
                            log.info("reserve product id: {}, quantity: {}", value.productId, value.quantity)
                        }
                    }
            }

            // payment pending
            val paymentResponse = paymentService.processPayment(order.id!!, order.getTotalPrice()).get()
            if (paymentResponse != null) {
                order.checkoutUrl = paymentResponse.checkoutUrl
                orderRepository.save(order)
                log.info("created payment id: {}, status: {}", paymentResponse.paymentId, paymentResponse.status)
            }

            Span.current().setAttribute("order.id", savedOrder.id.toString())
            log.debug("Все успешно сохранено")
            return savedOrder
        } catch (e: Exception) {
            val cause = if (e.cause != null) e.cause else e
            log.error("Ошибка при создание заказа {}", cause?.message)
            throw OrderCreateException("Order created error: ${cause?.message}")
        } finally {
            MDC.clear()
        }
    }

    override fun confirm(request: ConfirmPaymentRequestDto): ConfirmPaymentResponseDto {
        try {
            val order = orderRepository.findById(request.orderId!!).orElseThrow {
                NotFoundOrderException("Order not found")
            }

            if (request.status != PaymentStatus.SUCCEEDED) {
                throw RuntimeException("Payment not successful")
            }

            if (request.amount != order.amount) {
                throw RuntimeException("Payment amount is not same!")
            }

            order.status = OrderStatus.PAID
            orderRepository.save(order)
            return ConfirmPaymentResponseDto(
                orderId = order.id,
                status = order.status,
                success = true,
                message = "Order confirmation successful",
            )

        } catch (e: Exception) {
            val cause = if (e.cause != null) e.cause else e
            log.error("Возникла ошибка в OrderService.confirm", cause)
            throw OrderConfirmException("Order confirm error: ${cause?.message}")
        }
    }

    override fun cancel(orderId: Long): CancelOrderResponseDto {
        try {
            val order = orderRepository.findById(orderId).orElseThrow {
                NotFoundOrderException("Order not found")
            }

            inventoryService.reserveCancel(order.id!!)
                .whenCompleteAsync { value, error ->
                    if (error != null) {
                        throw RuntimeException("failed to reserve cancel order", error)
                    } else if (value != null) {
                        order.status = OrderStatus.CANCELLED
                        orderRepository.save(order)
                    }
                }

            return CancelOrderResponseDto(
                orderId = order.id,
                success = true,
                message = "Order cancelled"
            )
        } catch (e: Exception) {
            val cause = if (e.cause != null) e.cause else e
            log.error("Возникла ошибка", cause)
            throw OrderCancelException("Order cancel error: ${cause?.message}")
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

            log.debug("Выпало число: {}", randomInt)

            if (randomInt < 30) {
                log.error("Возникли проблемы с обработкой сохранения заказа")
                throw RuntimeException("Типа проблемы с обработкой заказа")
            }

            if (randomInt > 70) {
                log.warn("OrderService замедлился")
                Thread.sleep(200)
            }
        }
    }
}