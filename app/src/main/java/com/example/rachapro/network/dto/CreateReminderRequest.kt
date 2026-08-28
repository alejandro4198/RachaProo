package com.example.rachapro.network.dto

data class CreateReminderRequest(
    val activityId: Long?,
    val title: String,
    val message: String,
    val triggerAtMillis: Long
)