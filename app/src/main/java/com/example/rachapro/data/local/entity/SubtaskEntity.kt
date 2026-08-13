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

    /*
     * Actividad a la cual pertenece.
     */
    val activityId: Long,

    /*
     * Contenido de la subtarea.
     */
    val title: String,

    /*
     * Estado individual.
     */
    val isCompleted: Boolean = false,

    /*
     * Auditoría básica.
     */
    val createdAt: Long,

    val updatedAt: Long,

    /*
     * Null mientras no haya sido completada.
     */
    val completedAt: Long? = null
)