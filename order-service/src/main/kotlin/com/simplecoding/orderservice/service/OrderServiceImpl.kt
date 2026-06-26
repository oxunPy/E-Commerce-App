package com.simplecoding.orderservice.service

import com.simplecoding.orderservice.domain.dto.CreateOrderRequestDto
import com.simplecoding.orderservice.domain.entity.Order
import com.simplecoding.orderservice.domain.entity.OrderItem
import com.simplecoding.orderservice.exception.NotFoundOrderException
import com.simplecoding.orderservice.exception.OrderCreatedException
import com.simplecoding.orderservice.repository.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository
) : OrderService {

    private val log = LoggerFactory.getLogger(OrderServiceImpl::class.java)

    @Transactional
    override fun create(request: CreateOrderRequestDto): Order {
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

            log.debug("Все успешно сохранено");
            return savedOrder
        } catch (e: Exception) {
            val cause = if (e.cause != null) e.cause else e
            log.error("Ошибка при создание заказа {}", cause?.message);
            throw OrderCreatedException("Order created error: ${cause?.message}");
        }
    }

    @Transactional(readOnly = true)
    override fun getOrderWithItems(id: Long): Order? {
        log.debug("В метод getOrderWithItems получен запрос поиска по id: {}", id)

        val order = orderRepository.findById(id).orElseThrow {
            NotFoundOrderException("закас не найден id: $id")
        }

        log.debug("Результат успешно найден");
        return order
    }
}