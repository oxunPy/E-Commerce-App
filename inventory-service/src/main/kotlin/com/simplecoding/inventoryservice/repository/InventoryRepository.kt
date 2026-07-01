package com.simplecoding.inventoryservice.repository

import com.simplecoding.inventoryservice.domain.entity.Inventory
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface InventoryRepository : JpaRepository<Inventory, Long> {
    fun findByProductId(id: String): Optional<Inventory>
}