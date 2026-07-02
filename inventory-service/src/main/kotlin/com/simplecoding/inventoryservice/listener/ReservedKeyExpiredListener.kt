package com.simplecoding.inventoryservice.listener

import com.simplecoding.inventoryservice.repository.InventoryReservationRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.stereotype.Component

@Component
class ReservedKeyExpiredListener(
    keyExpirationListenerContainer: RedisMessageListenerContainer,
    private val inventoryReservationRepository: InventoryReservationRepository
): MessageListener {

    companion object {
        private val log = LoggerFactory.getLogger(ReservedKeyExpiredListener::class.java)
    }

    init {
        keyExpirationListenerContainer.addMessageListener(
            this,
            PatternTopic("__keyevent@0__:expired")
        )
    }

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val expiredKey = message.toString()
        if (!expiredKey.startsWith("reservation:expire:")) return

        val reservationId = expiredKey.substringAfterLast(":").toLongOrNull() ?: return

        try {
            inventoryReservationRepository.deleteById(reservationId)
            log.info("Просроченное бронирование удалено: {}", reservationId)
        } catch (e: Exception) {
            log.error("Ошибка при удалении бронирования: {}", reservationId, e)
        }
    }

}