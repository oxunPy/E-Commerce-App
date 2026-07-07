package com.simplecoding.orderservice.outbox

import com.fasterxml.jackson.annotation.JsonFormat
import com.simplecoding.orderservice.dictionary.OutboxStatus
import com.simplecoding.orderservice.domain.event.OrderCreatedEvent
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.type.SqlTypes
import java.time.ZonedDateTime

@Entity
@Table(name = "outbox_event")
class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "aggregation_type", nullable = false)
    var aggregationType: String? = null

    @Column(name = "aggregation_id", nullable = false)
    var aggregateId: String? = null

    @Column(name = "event_type", nullable = false)
    var eventType: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "JSON", nullable = false)
    var payload: String? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OutboxStatus? = null

    @Column(name = "trace_id", nullable = false)
    var traceId: String? = null

    @Column(name = "span_id", nullable = false)
    var spanId: String? = null

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    var createdAt: ZonedDateTime? = null

    @UpdateTimestamp
    @Column(name = "processed_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    var processedAt: ZonedDateTime? = null

    @Column(name = "traceparent", nullable = false)
    var traceparent: String? = null
}