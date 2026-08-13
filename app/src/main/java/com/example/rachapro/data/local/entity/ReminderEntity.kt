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

    /*
     * Usuario propietario.
     */
    val userId: Long,

    /*
     * Puede ser null para soportar
     * recordatorios independientes.
     */
    val activityId: Long? = null,

    /*
     * Título visible de la notificación.
     */
    val title: String,

    /*
     * Texto adicional.
     */
    val message: String = "",

    /*
     * Momento exacto en el que debe
     * activarse el recordatorio.
     */
    val triggerAtMillis: Long,

    /*
     * SCHEDULED
     * DELIVERED
     * CANCELLED
     */
    val status: String =
        ReminderStatus.SCHEDULED,

    val createdAt: Long,

    val updatedAt: Long,

    /*
     * Solo tendrá valor después
     * de mostrarse la notificación.
     */
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