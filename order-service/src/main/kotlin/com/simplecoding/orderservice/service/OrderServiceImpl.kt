package com.simplecoding.orderservice.service

import com.simplecoding.orderservice.domain.dto.CreateOrderRequestDto
import com.simplecoding.orderservice.domain.entity.Order
import com.simplecoding.orderservice.domain.entity.OrderItem
import com.simplecoding.orderservice.repository.OrderRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository
) : OrderService {

    private val log = LoggerFactory.getLogger(OrderServiceImpl::class.java)

    @Transactional
    override fun create(request: CreateOrderRequestDto): Order {
        log.debug("В метод OrderService.create получен запрос: {}", request)

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

        return savedOrder

    }

}