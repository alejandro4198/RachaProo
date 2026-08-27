package com.example.rachapro.backend.achievement

import com.example.rachapro.backend.achievement.dto.AchievementResponse
import com.example.rachapro.backend.achievement.dto.CreateAchievementRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/achievements")
class AchievementController(
    private val achievementService: AchievementService
) {

    @GetMapping
    fun findAll(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<AchievementResponse>> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        return ResponseEntity.ok(
            achievementService.findAllByUserId(userId)
        )
    }

    @PostMapping
    fun create(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: CreateAchievementRequest
    ): ResponseEntity<AchievementResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val achievement = achievementService.create(
            userId = userId,
            request = request
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(achievement)
    }
}