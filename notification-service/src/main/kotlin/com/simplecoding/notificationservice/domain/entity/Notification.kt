package com.simplecoding.notificationservice.domain.entity

import com.simplecoding.notificationservice.domain.NotificationStatus
import com.simplecoding.notificationservice.domain.NotificationType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@Document(collection = "notifications")
class Notification() {
    @Id
    var id: String? = null

    var type: NotificationType = NotificationType.EMAIL

    var status: NotificationStatus = NotificationStatus.PENDING

    @Field(name = "order_id")
    var orderId: Long? = null

    var event: String? = null

    var message: String? = null

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor(orderId: Long, event: String, message: String): this() {
        this.orderId = orderId
        this.event = event
        this.message = message
    }
}