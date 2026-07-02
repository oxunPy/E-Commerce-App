package com.simplecoding.inventoryservice.service

import com.simplecoding.inventoryservice.domain.dto.CancelReservationRequestDto
import com.simplecoding.inventoryservice.domain.dto.CancelReservationResponseDto
import com.simplecoding.inventoryservice.domain.dto.CompleteReservationRequestDto
import com.simplecoding.inventoryservice.domain.dto.CompleteReservationResponseDto
import com.simplecoding.inventoryservice.domain.dto.CreateInventoryRequestDto
import com.simplecoding.inventoryservice.domain.dto.CreateInventoryResponseDto
import com.simplecoding.inventoryservice.domain.dto.InventoryDto
import com.simplecoding.inventoryservice.domain.dto.InventoryReservationDto
import com.simplecoding.inventoryservice.domain.dto.ReserveProductRequestDto
import com.simplecoding.inventoryservice.domain.entity.Inventory

interface InventoryService {
    fun create(request: CreateInventoryRequestDto): CreateInventoryResponseDto

    fun reserve(request: ReserveProductRequestDto): InventoryReservationDto

    fun completeReserve(request: CompleteReservationRequestDto): CompleteReservationResponseDto

    fun cancelReservation(request: CancelReservationRequestDto): CancelReservationResponseDto

    fun getInventoryList(): List<InventoryDto>
}