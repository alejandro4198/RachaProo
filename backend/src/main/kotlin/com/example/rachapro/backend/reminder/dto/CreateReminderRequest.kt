package com.example.rachapro.backend.reminder.dto

data class CreateReminderRequest(
    val activityId: Long? = null,
    val title: String,
    val message: String,
    val triggerAtMillis: Long
)