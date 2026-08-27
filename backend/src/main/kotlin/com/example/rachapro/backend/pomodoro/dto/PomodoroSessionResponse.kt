package com.example.rachapro.backend.pomodoro.dto

data class PomodoroSessionResponse(
    val id: Long,
    val activityId: Long?,
    val type: String,
    val plannedDurationSeconds: Int,
    val status: String,
    val startedAtMillis: Long,
    val pausedAtMillis: Long?,
    val totalPausedMillis: Long,
    val completedAtMillis: Long?,
    val completedDateEpochDay: Long?,
    val createdAt: Long,
    val updatedAt: Long
)