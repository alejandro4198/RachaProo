package com.example.rachapro.data.repository

import com.example.rachapro.data.local.dao.AchievementDao
import com.example.rachapro.data.local.entity.AchievementEntity
import com.example.rachapro.domain.AchievementCheckInput
import com.example.rachapro.domain.AchievementEngine
import com.example.rachapro.network.ApiService
import com.example.rachapro.network.dto.AchievementResponse
import com.example.rachapro.network.dto.CreateAchievementRequest
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class AchievementRepository(
    private val achievementDao: AchievementDao,
    private val apiService: ApiService
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

        try {
            val remoteAchievements =
                apiService.getAchievements()

            remoteAchievements.forEach { response ->
                saveIfChanged(
                    userId = userId,
                    response = response
                )
            }

            val remoteTypes =
                remoteAchievements
                    .map { it.type }
                    .toMutableSet()

            val typesToUnlock =
                AchievementEngine.typesToUnlock(
                    input = input
                )

            typesToUnlock.forEach { type ->

                if (type !in remoteTypes) {

                    try {
                        val response =
                            apiService.createAchievement(
                                CreateAchievementRequest(
                                    type = type
                                )
                            )

                        saveIfChanged(
                            userId = userId,
                            response = response
                        )

                        remoteTypes.add(type)

                    } catch (e: HttpException) {

                        if (e.code() == 409) {
                            syncRemoteAchievements(
                                userId = userId
                            )
                        }
                    }
                }
            }

        } catch (_: Exception) {
        }
    }

    suspend fun syncRemoteAchievements(
        userId: Long
    ) {

        try {
            val achievements =
                apiService.getAchievements()

            achievements.forEach { response ->
                saveIfChanged(
                    userId = userId,
                    response = response
                )
            }

        } catch (_: Exception) {
        }
    }

    private suspend fun saveIfChanged(
        userId: Long,
        response: AchievementResponse
    ) {

        val existing =
            achievementDao.getAchievement(
                userId = userId,
                type = response.type
            )

        if (
            existing == null ||
            existing.id != response.id ||
            existing.unlockedAt != response.unlockedAt
        ) {

            achievementDao.upsertAchievement(
                response.toEntity(userId)
            )
        }
    }

    private fun AchievementResponse.toEntity(
        userId: Long
    ): AchievementEntity {

        return AchievementEntity(
            id = id,
            userId = userId,
            type = type,
            unlockedAt = unlockedAt
        )
    }
}