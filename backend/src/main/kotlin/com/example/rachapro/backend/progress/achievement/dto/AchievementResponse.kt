package com.example.rachapro.backend.progress.achievement.dto

data class AchievementResponse(
    val id: Long,
    val type: String,
    val unlockedAt: Long
)