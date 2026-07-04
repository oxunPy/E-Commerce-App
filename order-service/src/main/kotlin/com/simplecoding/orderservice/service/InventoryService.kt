package com.simplecoding.orderservice.service

import com.simplecoding.orderservice.domain.dto.CancelReservationResponseDto
import com.simplecoding.orderservice.domain.dto.CompleteReservationResponseDto
import com.simplecoding.orderservice.domain.dto.InventoryReservationResponseDto
import java.util.concurrent.CompletableFuture

interface InventoryService {
    fun reserveProduct(productId: String, orderId: Long, quantity: Int): CompletableFuture<InventoryReservationResponseDto?>

    fun reserveComplete(orderId: Long): CompletableFuture<CompleteReservationResponseDto?>

    fun reserveCancel(orderId: Long): CompletableFuture<CancelReservationResponseDto?>
}