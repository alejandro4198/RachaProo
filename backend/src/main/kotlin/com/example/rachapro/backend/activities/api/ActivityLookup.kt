package com.example.rachapro.backend.activities.api

interface ActivityLookup {
    fun existsActiveActivityForUser(
        activityId: Long,
        userId: Long
    ): Boolean
}