package com.simplecoding.inventoryservice.controller

import com.simplecoding.inventoryservice.domain.dto.CancelReservationRequestDto
import com.simplecoding.inventoryservice.domain.dto.CancelReservationResponseDto
import com.simplecoding.inventoryservice.domain.dto.CompleteReservationRequestDto
import com.simplecoding.inventoryservice.domain.dto.CompleteReservationResponseDto
import com.simplecoding.inventoryservice.domain.dto.CreateInventoryRequestDto
import com.simplecoding.inventoryservice.domain.dto.CreateInventoryResponseDto
import com.simplecoding.inventoryservice.domain.dto.InventoryReservationDto
import com.simplecoding.inventoryservice.domain.dto.ReserveProductRequestDto
import com.simplecoding.inventoryservice.service.InventoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/inventory")
class InventoryController(
    private val inventoryService: InventoryService
) {

    @PostMapping("/create")
    fun createInventory(@RequestBody request: CreateInventoryRequestDto): ResponseEntity<CreateInventoryResponseDto> {
        return ResponseEntity.ok(
            CreateInventoryResponseDto.fromInventory(
                inventoryService.create(request)
            )
        )
    }

    @PostMapping("/reserve")
    fun reserveProduct(@RequestBody request: ReserveProductRequestDto): ResponseEntity<InventoryReservationDto> {
        return ResponseEntity.ok(
            InventoryReservationDto.fromInventoryReservation(
                inventoryService.reserve(request)
            )
        )
    }

    @PutMapping("/reserve/complete")
    fun completeReservation(@RequestBody request: CompleteReservationRequestDto) : ResponseEntity<CompleteReservationResponseDto> {
        return ResponseEntity.ok(
            inventoryService.completeReserve(request)
        )
    }

    @PutMapping("/reserve/cancel")
    fun cancelReservation(@RequestBody request: CancelReservationRequestDto) : ResponseEntity<CancelReservationResponseDto> {
        return ResponseEntity.ok(
            inventoryService.cancelReservation(request)
        )
    }
}