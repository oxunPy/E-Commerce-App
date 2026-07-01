package com.simplecoding.orderservice.controller

import com.simplecoding.orderservice.domain.dto.CreateOrderRequestDto
import com.simplecoding.orderservice.domain.dto.CreateOrderResponseDto
import com.simplecoding.orderservice.service.OrderService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
    fun createOrder(@Valid @RequestBody request: CreateOrderRequestDto): ResponseEntity<CreateOrderResponseDto> {
        return ResponseEntity.ok(
            CreateOrderResponseDto.fromOrder(
                orderService.create(request)
            )
        )
    }

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable("id") orderId: Long): ResponseEntity<CreateOrderResponseDto> {
        return ResponseEntity.ok(
            CreateOrderResponseDto.fromOrder(
                orderService.getOrderWithItems(orderId)
            )
        )
    }
}