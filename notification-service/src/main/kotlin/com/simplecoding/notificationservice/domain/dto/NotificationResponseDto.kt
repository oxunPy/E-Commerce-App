package com.simplecoding.notificationservice.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.simplecoding.notificationservice.domain.entity.Notification
import java.time.LocalDateTime


data class NotificationResponseDto(
    @JsonProperty("notification_id")
    val notificationId: String?,
    @JsonProperty("message")
    val message: String?,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(notification: Notification): NotificationResponseDto {
            return NotificationResponseDto(
                notificationId = notification.id,
                message = notification.message,
                createdAt = notification.createdAt,
            )
        }
    }
}