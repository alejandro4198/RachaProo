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

    /*
     * Fecha local de vencimiento.
     *
     * Se guarda como Epoch Day para mantener
     * el concepto de "día" sin depender
     * directamente de una zona horaria.
     */
    val dueDateEpochDay: Long,

    /*
     * Minutos transcurridos desde las 00:00.
     *
     * Ejemplo:
     * 14:30 = 870 minutos.
     *
     * null significa que la actividad
     * no tiene una hora específica.
     */
    val dueTimeMinutes: Int? = null,

    val priority: String,

    val status: String,

    /*
     * Preparado para repetición futura.
     *
     * Ejemplos futuros:
     * DAILY
     * WEEKLY
     *
     * null = no se repite.
     */
    val repeatRule: String? = null,

    val createdAt: Long,

    val updatedAt: Long,

    val completedAt: Long? = null,

    /*
     * Día local en el que se completó.
     *
     * Será especialmente útil para
     * calcular las rachas.
     */
    val completedDateEpochDay: Long? = null,

    /*
     * Soft delete.
     *
     * Las actividades no se eliminan
     * físicamente de inmediato.
     */
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