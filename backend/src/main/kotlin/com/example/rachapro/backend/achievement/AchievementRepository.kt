package com.example.rachapro.backend.achievement

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