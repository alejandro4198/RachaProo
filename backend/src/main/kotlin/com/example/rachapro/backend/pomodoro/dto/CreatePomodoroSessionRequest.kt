package com.example.rachapro.backend.pomodoro.dto

data class CreatePomodoroSessionRequest(
    val activityId: Long? = null,
    val type: String,
    val plannedDurationSeconds: Int
)