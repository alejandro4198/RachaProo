package com.example.rachapro.backend.subtask

import com.example.rachapro.backend.subtask.dto.CreateSubtaskRequest
import com.example.rachapro.backend.subtask.dto.SubtaskResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PatchMapping
import com.example.rachapro.backend.subtask.dto.UpdateSubtaskRequest
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping

@RestController
@RequestMapping("/api/activities/{activityId}/subtasks")
class SubtaskController(
    private val subtaskService: SubtaskService
) {

    @GetMapping
    fun findAll(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable activityId: Long
    ): ResponseEntity<List<SubtaskResponse>> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val subtasks = subtaskService.findAll(
            userId = userId,
            activityId = activityId
        ) ?: return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build()

        return ResponseEntity.ok(subtasks)
    }

    @PostMapping
    fun create(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable activityId: Long,
        @RequestBody request: CreateSubtaskRequest
    ): ResponseEntity<SubtaskResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val subtask = subtaskService.create(
            userId = userId,
            activityId = activityId,
            request = request
        ) ?: return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build()

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(subtask)
    }

    @PatchMapping("/{subtaskId}/complete")
    fun complete(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable activityId: Long,
        @PathVariable subtaskId: Long
    ): ResponseEntity<SubtaskResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val subtask = subtaskService.complete(
            userId = userId,
            activityId = activityId,
            subtaskId = subtaskId
        ) ?: return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build()

        return ResponseEntity.ok(subtask)
    }

    @PutMapping("/{subtaskId}")
    fun update(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable activityId: Long,
        @PathVariable subtaskId: Long,
        @RequestBody request: UpdateSubtaskRequest
    ): ResponseEntity<SubtaskResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val subtask = subtaskService.update(
            userId = userId,
            activityId = activityId,
            subtaskId = subtaskId,
            request = request
        ) ?: return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build()

        return ResponseEntity.ok(subtask)
    }

    @DeleteMapping("/{subtaskId}")
    fun delete(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable activityId: Long,
        @PathVariable subtaskId: Long
    ): ResponseEntity<Void> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val deleted = subtaskService.delete(
            userId = userId,
            activityId = activityId,
            subtaskId = subtaskId
        )

        if (!deleted) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build()
        }

        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{subtaskId}/uncomplete")
    fun uncomplete(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable activityId: Long,
        @PathVariable subtaskId: Long
    ): ResponseEntity<SubtaskResponse> {

        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val subtask =
            subtaskService.uncomplete(
                userId = userId,
                activityId = activityId,
                subtaskId = subtaskId
            )
                ?: return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build()

        return ResponseEntity.ok(subtask)
    }
}