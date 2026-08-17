package com.example.rachapro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rachapro.data.local.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    @Query(
        """
        SELECT *
        FROM achievements
        WHERE userId = :userId
        ORDER BY unlockedAt ASC
        """
    )
    fun observeAchievements(
        userId: Long
    ): Flow<List<AchievementEntity>>

    @Query(
        """
        SELECT *
        FROM achievements
        WHERE userId = :userId
        AND type = :type
        LIMIT 1
        """
    )
    suspend fun getAchievement(
        userId: Long,
        type: String
    ): AchievementEntity?

    @Insert(
        onConflict = OnConflictStrategy.IGNORE
    )
    suspend fun insertAchievement(
        achievement: AchievementEntity
    ): Long
}
