package com.example.rachapro.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    indices = [
        Index(
            value = ["userId"]
        ),
        Index(
            value = ["activityId"]
        ),
        Index(
            value = ["status"]
        ),
        Index(
            value = ["triggerAtMillis"]
        ),
        Index(
            value = [
                "userId",
                "status",
                "triggerAtMillis"
            ]
        )
    ]
)
data class ReminderEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: Long,

    val activityId: Long? = null,

    val title: String,

    val message: String = "",

    val triggerAtMillis: Long,

    val status: String =
        ReminderStatus.SCHEDULED,

    val createdAt: Long,

    val updatedAt: Long,

    val deliveredAt: Long? = null
)

object ReminderStatus {

    const val SCHEDULED =
        "SCHEDULED"

    const val DELIVERED =
        "DELIVERED"

    const val CANCELLED =
        "CANCELLED"
}