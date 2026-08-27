package com.example.rachapro.backend.achievement

import com.example.rachapro.backend.achievement.dto.AchievementResponse
import com.example.rachapro.backend.achievement.dto.CreateAchievementRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.example.rachapro.backend.error.ConflictException

@Service
class AchievementService(
    private val achievementRepository: AchievementRepository
) {

    private val validTypes = setOf(
        "FIRST_ACTIVITY_COMPLETED",
        "FIRST_FOCUS_POMODORO",
        "STREAK_3_DAYS",
        "STREAK_7_DAYS",
        "ACTIVITIES_10_COMPLETED",
        "POMODOROS_10_COMPLETED"
    )

    @Transactional(readOnly = true)
    fun findAllByUserId(userId: Long): List<AchievementResponse> {
        return achievementRepository
            .findAllByUserIdOrderByUnlockedAtAsc(userId)
            .map { it.toResponse() }
    }

    @Transactional
    fun create(
        userId: Long,
        request: CreateAchievementRequest
    ): AchievementResponse {

        val type = request.type.trim().uppercase()

        require(type in validTypes) {
            "Tipo de logro no valido"
        }

        if (achievementRepository.existsByUserIdAndType(userId, type)) {
            throw ConflictException("El logro ya fue desbloqueado")
        }

        val achievement = AchievementEntity(
            userId = userId,
            type = type,
            unlockedAt = System.currentTimeMillis()
        )

        return achievementRepository
            .save(achievement)
            .toResponse()
    }

    private fun AchievementEntity.toResponse(): AchievementResponse {
        return AchievementResponse(
            id = id,
            type = type,
            unlockedAt = unlockedAt
        )
    }
}