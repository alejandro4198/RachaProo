package com.example.rachapro.backend.activity

import com.example.rachapro.backend.activity.dto.ActivityResponse
import com.example.rachapro.backend.activity.dto.CreateActivityRequest
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
import com.example.rachapro.backend.activity.dto.UpdateActivityRequest
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping("/api/activities")
class ActivityController(
    private val activityService: ActivityService
) {

    @GetMapping
    fun findAll(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<ActivityResponse>> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        return ResponseEntity.ok(
            activityService.findAllByUserId(userId)
        )
    }

    @GetMapping("/paged")
    fun findPage(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "100") size: Int
    ): ResponseEntity<List<ActivityResponse>> {

        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        if (page < 0 || size !in 1..200) {
            return ResponseEntity.badRequest().build()
        }

        return ResponseEntity.ok(
            activityService.findPageByUserId(
                userId = userId,
                page = page,
                size = size
            )
        )
    }

    @GetMapping("/{id}")
    fun findById(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: Long
    ): ResponseEntity<ActivityResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val activity = activityService.findById(userId, id)
            ?: return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build()

        return ResponseEntity.ok(activity)
    }

    @PostMapping
    fun create(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: CreateActivityRequest
    ): ResponseEntity<ActivityResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val activity = activityService.create(userId, request)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(activity)
    }

    @PutMapping("/{id}")
    fun update(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: Long,
        @RequestBody request: UpdateActivityRequest
    ): ResponseEntity<ActivityResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val activity = activityService.update(
            userId = userId,
            activityId = id,
            request = request
        ) ?: return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build()

        return ResponseEntity.ok(activity)
    }

    @PatchMapping("/refresh-statuses")
    fun refreshStatuses(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<ActivityResponse>> {

        val userId =
            jwt.subject
                ?.toLongOrNull()
                ?: return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build()

        return ResponseEntity.ok(
            activityService.refreshStatuses(
                userId = userId
            )
        )
    }

    @PatchMapping("/{id}/complete")
    fun complete(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: Long
    ): ResponseEntity<ActivityResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val activity = activityService.complete(
            userId = userId,
            activityId = id
        ) ?: return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .build()

        return ResponseEntity.ok(activity)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val deleted = activityService.delete(
            userId = userId,
            activityId = id
        )

        if (!deleted) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build()
        }

        return ResponseEntity.noContent().build()
    }
}