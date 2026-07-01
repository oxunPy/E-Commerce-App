package com.simplecoding.inventoryservice.repository

import com.simplecoding.inventoryservice.domain.entity.InventoryHistory
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryHistoryRepository: JpaRepository<InventoryHistory, Long>