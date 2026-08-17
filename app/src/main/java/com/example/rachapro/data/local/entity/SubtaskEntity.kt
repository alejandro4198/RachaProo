package com.example.rachapro.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subtasks",
    indices = [
        Index(
            value = ["activityId"]
        ),
        Index(
            value = [
                "activityId",
                "isCompleted"
            ]
        )
    ]
)
data class SubtaskEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val activityId: Long,

    val title: String,

    val isCompleted: Boolean = false,

    val createdAt: Long,

    val updatedAt: Long,

    val completedAt: Long? = null
)