package com.simplecoding.orderservice.unittest

import com.simplecoding.orderservice.repository.OrderRepository
import com.simplecoding.orderservice.service.OrderService
import com.simplecoding.orderservice.service.OrderServiceImpl
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [
    OrderServiceImpl::class
])
class OrderServiceTest {
    @Autowired
    private lateinit var orderService: OrderService

    @MockitoBean
    private lateinit var orderRepository: OrderRepository


    fun createOrderTest() {

    }
}