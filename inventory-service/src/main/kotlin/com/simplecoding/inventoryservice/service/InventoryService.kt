package com.simplecoding.inventoryservice.service

import com.simplecoding.inventoryservice.domain.dto.CancelReservationRequestDto
import com.simplecoding.inventoryservice.domain.dto.CancelReservationResponseDto
import com.simplecoding.inventoryservice.domain.dto.CompleteReservationRequestDto
import com.simplecoding.inventoryservice.domain.dto.CompleteReservationResponseDto
import com.simplecoding.inventoryservice.domain.dto.CreateInventoryRequestDto
import com.simplecoding.inventoryservice.domain.dto.ReserveProductRequestDto
import com.simplecoding.inventoryservice.domain.entity.Inventory
import com.simplecoding.inventoryservice.domain.entity.InventoryReservation

interface InventoryService {
    fun create(request: CreateInventoryRequestDto): Inventory

    fun reserve(request: ReserveProductRequestDto): InventoryReservation

    fun completeReserve(request: CompleteReservationRequestDto): CompleteReservationResponseDto

    fun cancelReservation(request: CancelReservationRequestDto): CancelReservationResponseDto
}