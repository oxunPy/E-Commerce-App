package com.simplecoding.inventoryservice.repository

import com.simplecoding.inventoryservice.domain.entity.InventoryReservation
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryReservationRepository: JpaRepository<InventoryReservation, Long> {
    fun findAllByInventoryId(id: Long): List<InventoryReservation>

    fun findAllByOrderId(id: Long): List<InventoryReservation>
}