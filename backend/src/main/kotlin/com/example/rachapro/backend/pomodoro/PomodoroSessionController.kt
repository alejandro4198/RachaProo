package com.example.rachapro.backend.pomodoro

import com.example.rachapro.backend.pomodoro.dto.CreatePomodoroSessionRequest
import com.example.rachapro.backend.pomodoro.dto.PomodoroSessionResponse
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
@RequestMapping("/api/pomodoro-sessions")
class PomodoroSessionController(
    private val pomodoroSessionService: PomodoroSessionService
) {

    @GetMapping
    fun findAll(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<PomodoroSessionResponse>> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        return ResponseEntity.ok(
            pomodoroSessionService.findAllByUserId(userId)
        )
    }

    @PostMapping
    fun create(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: CreatePomodoroSessionRequest
    ): ResponseEntity<PomodoroSessionResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val session = pomodoroSessionService.create(
            userId = userId,
            request = request
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(session)
    }

    @PatchMapping("/{id}/pause")
    fun pause(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: Long
    ): ResponseEntity<PomodoroSessionResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val session = pomodoroSessionService.pause(
            userId = userId,
            sessionId = id
        ) ?: return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build()

        return ResponseEntity.ok(session)
    }

    @PatchMapping("/{id}/resume")
    fun resume(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: Long
    ): ResponseEntity<PomodoroSessionResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val session = pomodoroSessionService.resume(
            userId = userId,
            sessionId = id
        ) ?: return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build()

        return ResponseEntity.ok(session)
    }

    @PatchMapping("/{id}/complete")
    fun complete(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: Long
    ): ResponseEntity<PomodoroSessionResponse> {

        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val session =
            pomodoroSessionService.complete(
                userId = userId,
                sessionId = id
            )
                ?: return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build()

        return ResponseEntity.ok(session)
    }

    @PatchMapping("/{id}/cancel")
    fun cancel(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: Long
    ): ResponseEntity<PomodoroSessionResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val session = pomodoroSessionService.cancel(
            userId = userId,
            sessionId = id
        ) ?: return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build()

        return ResponseEntity.ok(session)
    }
}