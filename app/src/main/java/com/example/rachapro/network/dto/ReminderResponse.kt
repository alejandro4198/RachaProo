package com.example.rachapro.network.dto

data class ReminderResponse(
    val id: Long,
    val activityId: Long?,
    val title: String,
    val message: String,
    val triggerAtMillis: Long,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
    val deliveredAt: Long?
)