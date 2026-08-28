package com.example.rachapro.network.dto

data class CreatePomodoroSessionRequest(
    val activityId: Long? = null,
    val type: String,
    val plannedDurationSeconds: Int
)