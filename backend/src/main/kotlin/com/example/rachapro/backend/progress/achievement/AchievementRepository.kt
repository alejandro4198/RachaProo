package com.example.rachapro.backend.progress.achievement

import org.springframework.data.jpa.repository.JpaRepository

interface AchievementRepository : JpaRepository<AchievementEntity, Long> {

    fun findAllByUserIdOrderByUnlockedAtAsc(
        userId: Long
    ): List<AchievementEntity>

    fun existsByUserIdAndType(
        userId: Long,
        type: String
    ): Boolean
}