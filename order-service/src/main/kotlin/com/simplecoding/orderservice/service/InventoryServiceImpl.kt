package com.simplecoding.orderservice.service

import com.simplecoding.orderservice.client.InventoryClient
import com.simplecoding.orderservice.domain.dto.CancelReservationResponseDto
import com.simplecoding.orderservice.domain.dto.CompleteReservationResponseDto
import com.simplecoding.orderservice.domain.dto.InventoryReservationResponseDto
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class InventoryServiceImpl(private val inventoryClient: InventoryClient) : InventoryService {

    override fun reserveProduct(
        productId: String,
        orderId: Long,
        quantity: Int
    ): CompletableFuture<InventoryReservationResponseDto?> {
        return inventoryClient.reserve(productId, orderId, quantity).toFuture()
    }

    override fun reserveComplete(orderId: Long): CompletableFuture<CompleteReservationResponseDto?> {
        return inventoryClient.reserveComplete(orderId).toFuture()
    }

    override fun reserveCancel(orderId: Long): CompletableFuture<CancelReservationResponseDto?> {
        return inventoryClient.reserveCancel(orderId).toFuture()
    }
}