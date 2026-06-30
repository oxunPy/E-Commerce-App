package com.simplecoding.notificationservice.service

import com.simplecoding.notificationservice.domain.NotificationStatus
import com.simplecoding.notificationservice.domain.dto.NotificationRequestDto
import com.simplecoding.notificationservice.domain.entity.Notification
import com.simplecoding.notificationservice.exception.NotificationCreateException
import com.simplecoding.notificationservice.metrics.annotation.BusinessMetric
import com.simplecoding.notificationservice.repository.NotificationRepository
import io.micrometer.observation.annotation.Observed
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

@Service
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
) : NotificationService {

    private val failureMode = AtomicBoolean(false)

    companion object {
        private val log = LoggerFactory.getLogger(NotificationServiceImpl::class.java)
    }

    @BusinessMetric(
        value = "notifications.create",
        tags = ["operation.create", "type=write"]
    )
    @Observed(name = "notification.creation", contextualName = "Create notification")
    override fun create(request: NotificationRequestDto): Notification {
        try {
            log.info("В метод Notification.create получен запрос : {}", request)
            if (failureMode.get()) {
                runFail(request.orderId)
            }

            val savedNotification = notificationRepository.save(
                Notification(
                    orderId = request.orderId,
                    event = request.event,
                    message = "Уведомление заказа id: ${request.orderId}",
                )
            )

            return savedNotification
        } catch (e: Exception) {
            val cause = if (e.cause != null) e.cause else e
            log.error("Ошибка при создание уведомление {}", cause?.message)
            throw NotificationCreateException("Notification created error: ${cause?.message}")
        }
    }

    override fun getPendingNotifications(): Int {
        return notificationRepository.findNotificationsByStatus(NotificationStatus.PENDING).size
    }

    fun runFail(orderId: Long) {
        val random = Random().nextInt(100)
        log.info("Выпало число: {}", random)

        if (random < 30) {
            log.error(
                "Возможно проблема с отправкой информации по заказу: {}",
                orderId
            )
            throw RuntimeException("Типа проблемы с отправкой уведомления")
        }

        if (random > 70) {
            log.warn("NotificationService замедлился")
            Thread.sleep(200)
        }
    }


}