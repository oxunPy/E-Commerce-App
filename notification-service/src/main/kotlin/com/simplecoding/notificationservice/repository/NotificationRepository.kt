package com.simplecoding.notificationservice.repository

import com.simplecoding.notificationservice.domain.NotificationStatus
import com.simplecoding.notificationservice.domain.entity.Notification
import org.springframework.data.mongodb.repository.MongoRepository

interface NotificationRepository: MongoRepository<Notification, Long> {
    fun findNotificationsByStatus(status: NotificationStatus): List<Notification>
}