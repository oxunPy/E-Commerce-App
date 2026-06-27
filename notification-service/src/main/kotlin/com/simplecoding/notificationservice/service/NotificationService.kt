package com.simplecoding.notificationservice.service

import com.simplecoding.notificationservice.domain.dto.NotificationRequestDto
import com.simplecoding.notificationservice.domain.entity.Notification

interface NotificationService {
    fun create(request: NotificationRequestDto): Notification

    fun getPendingNotifications(): Int
}