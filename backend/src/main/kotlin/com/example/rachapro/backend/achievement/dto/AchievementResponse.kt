package com.example.rachapro.backend.achievement.dto

data class AchievementResponse(
    val id: Long,
    val type: String,
    val unlockedAt: Long
)