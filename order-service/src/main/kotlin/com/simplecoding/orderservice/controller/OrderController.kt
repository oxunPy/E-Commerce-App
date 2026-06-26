package com.simplecoding.orderservice.controller

import com.simplecoding.orderservice.domain.dto.CreateOrderRequestDto
import com.simplecoding.orderservice.domain.dto.OrderResponseDto
import com.simplecoding.orderservice.service.OrderService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
class OrderController {
    @Autowired
    private lateinit var orderService: OrderService

    @PostMapping("/create")
    fun createOrder(@Valid @RequestBody request: CreateOrderRequestDto): ResponseEntity<OrderResponseDto> {
        return ResponseEntity.ok(
            OrderResponseDto.fromOrder(
                orderService.create(request)
            )
        )
    }
}