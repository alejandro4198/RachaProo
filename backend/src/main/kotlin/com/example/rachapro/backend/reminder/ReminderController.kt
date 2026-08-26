package com.example.rachapro.backend.reminder

import com.example.rachapro.backend.reminder.dto.CreateReminderRequest
import com.example.rachapro.backend.reminder.dto.ReminderResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable

@RestController
@RequestMapping("/api/reminders")
class ReminderController(
    private val reminderService: ReminderService
) {

    @GetMapping
    fun findAll(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<ReminderResponse>> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        return ResponseEntity.ok(
            reminderService.findAllByUserId(userId)
        )
    }

    @PostMapping
    fun create(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: CreateReminderRequest
    ): ResponseEntity<ReminderResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val reminder = reminderService.create(
            userId = userId,
            request = request
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(reminder)
    }

    @PatchMapping("/{id}/delivered")
    fun markDelivered(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: Long
    ): ResponseEntity<ReminderResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val reminder = reminderService.markDelivered(
            userId = userId,
            reminderId = id
        ) ?: return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build()

        return ResponseEntity.ok(reminder)
    }

    @PatchMapping("/{id}/cancel")
    fun cancel(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: Long
    ): ResponseEntity<ReminderResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val reminder = reminderService.cancel(
            userId = userId,
            reminderId = id
        ) ?: return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build()

        return ResponseEntity.ok(reminder)
    }
}