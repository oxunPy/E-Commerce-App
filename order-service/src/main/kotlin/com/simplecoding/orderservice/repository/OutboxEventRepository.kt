package com.simplecoding.orderservice.repository

import com.simplecoding.orderservice.dictionary.OutboxStatus
import com.simplecoding.orderservice.outbox.OutboxEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OutboxEventRepository : JpaRepository<OutboxEvent, Long> {

    @Modifying
    @Query(
        value = """
            UPDATE outbox_event
            SET status = 'PROCESSING', processed_at = now()
            WHERE id IN (
                SELECT id FROM outbox_event
                WHERE status = 'NEW'
                ORDER BY created_at
                LIMIT :limit
                FOR UPDATE SKIP LOCKED
            )
            RETURNING *
        """,
        nativeQuery = true
    )
    fun claimNewEvents(@Param("limit") limit: Int): List<OutboxEvent>

    @Modifying
    @Query(
        value = """
            UPDATE OutboxEvent o 
            SET o.status = :status, o.processedAt = CURRENT_TIMESTAMP 
            WHERE o.aggregateId = :eventId 
            AND o.status = 'NEW' 
        """
    )
    fun updateStatus(
        @Param("eventId") eventId: Long?,
        @Param("status") status: OutboxStatus?
    ): Int

    fun countByStatus(status: OutboxStatus?): Long
}