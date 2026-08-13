package com.example.rachapro.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pomodoro_sessions",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["activityId"]),
        Index(value = ["status"]),
        Index(value = ["completedDateEpochDay"]),
        Index(value = ["userId", "status"])
    ]
)
data class PomodoroSessionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: Long,

    val activityId: Long? = null,

    val type: String =
        PomodoroSessionType.FOCUS,

    val plannedDurationSeconds: Int,

    val status: String =
        PomodoroSessionStatus.RUNNING,

    val startedAtMillis: Long,

    val pausedAtMillis: Long? = null,

    val totalPausedMillis: Long = 0L,

    val completedAtMillis: Long? = null,

    val completedDateEpochDay: Long? = null,

    val createdAt: Long,

    val updatedAt: Long
)

object PomodoroSessionType {

    const val FOCUS =
        "FOCUS"

    const val SHORT_BREAK =
        "SHORT_BREAK"

    const val LONG_BREAK =
        "LONG_BREAK"
}

object PomodoroSessionStatus {

    const val RUNNING =
        "RUNNING"

    const val PAUSED =
        "PAUSED"

    const val COMPLETED =
        "COMPLETED"

    const val CANCELLED =
        "CANCELLED"
}