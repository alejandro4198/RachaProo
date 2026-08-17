package com.example.rachapro.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "activities",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["categoryId"]),
        Index(value = ["status"]),
        Index(value = ["dueDateEpochDay"]),
        Index(
            value = [
                "userId",
                "isDeleted",
                "dueDateEpochDay"
            ]
        )
    ]
)
data class ActivityEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: Long,

    val categoryId: Long,

    val title: String,

    val description: String = "",

    val dueDateEpochDay: Long,

    val dueTimeMinutes: Int? = null,

    val priority: String,

    val status: String,

    val repeatRule: String? = null,

    val createdAt: Long,

    val updatedAt: Long,

    val completedAt: Long? = null,

    val completedDateEpochDay: Long? = null,

    val isDeleted: Boolean = false,

    val deletedAt: Long? = null
)

object ActivityStatus {

    const val PENDING =
        "PENDING"

    const val OVERDUE =
        "OVERDUE"

    const val COMPLETED =
        "COMPLETED"
}

object ActivityPriority {

    const val LOW =
        "LOW"

    const val MEDIUM =
        "MEDIUM"

    const val HIGH =
        "HIGH"
}