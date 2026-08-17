package com.example.rachapro.data.repository

import com.example.rachapro.data.local.dao.AchievementDao
import com.example.rachapro.data.local.entity.AchievementEntity
import com.example.rachapro.domain.AchievementCheckInput
import com.example.rachapro.domain.AchievementEngine
import kotlinx.coroutines.flow.Flow

class AchievementRepository(
    private val achievementDao: AchievementDao
) {

    fun observeAchievements(
        userId: Long
    ): Flow<List<AchievementEntity>> {

        return achievementDao.observeAchievements(
            userId = userId
        )
    }

    suspend fun syncAchievements(
        userId: Long,
        input: AchievementCheckInput
    ) {

        val typesToUnlock =
            AchievementEngine.typesToUnlock(
                input = input
            )

        val now =
            System.currentTimeMillis()

        typesToUnlock.forEach { type ->

            val existing =
                achievementDao.getAchievement(
                    userId = userId,
                    type = type
                )

            if (existing == null) {

                achievementDao.insertAchievement(
                    AchievementEntity(
                        userId = userId,
                        type = type,
                        unlockedAt = now
                    )
                )
            }
        }
    }
}
