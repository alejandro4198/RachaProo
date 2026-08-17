package com.example.rachapro.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "achievements",
    indices = [
        Index(value = ["userId"]),
        Index(
            value = ["userId", "type"],
            unique = true
        )
    ]
)
data class AchievementEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: Long,

    val type: String,

    val unlockedAt: Long
)

object AchievementType {

    const val FIRST_ACTIVITY_COMPLETED =
        "FIRST_ACTIVITY_COMPLETED"

    const val FIRST_FOCUS_POMODORO =
        "FIRST_FOCUS_POMODORO"

    const val STREAK_3_DAYS =
        "STREAK_3_DAYS"

    const val STREAK_7_DAYS =
        "STREAK_7_DAYS"

    const val ACTIVITIES_10_COMPLETED =
        "ACTIVITIES_10_COMPLETED"

    const val POMODOROS_10_COMPLETED =
        "POMODOROS_10_COMPLETED"
}
