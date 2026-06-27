package com.simplecoding.notificationservice.controller

import com.simplecoding.notificationservice.domain.dto.NotificationRequestDto
import com.simplecoding.notificationservice.domain.dto.NotificationResponseDto
import com.simplecoding.notificationservice.domain.entity.Notification
import com.simplecoding.notificationservice.service.NotificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController (
    private val notificationService: NotificationService
){
    @PostMapping
    fun notify(@RequestBody request: NotificationRequestDto): ResponseEntity<NotificationResponseDto> {

        return ResponseEntity.ok(
            NotificationResponseDto.from(
                notificationService.create(request)
            )
        )
    }

    @GetMapping("/pending")
    fun getPendingNotifications(): ResponseEntity<Int> {
        return ResponseEntity.ok(
            notificationService.getPendingNotifications()
        )
    }
}