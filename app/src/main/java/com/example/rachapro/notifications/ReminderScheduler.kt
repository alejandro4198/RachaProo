package com.example.rachapro.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.rachapro.data.local.entity.ReminderEntity

class ReminderScheduler(
    private val context: Context
) {

    private val alarmManager =
        context.getSystemService(
            AlarmManager::class.java
        )

    /*
     * =========================================================
     * PROGRAMAR RECORDATORIO
     * =========================================================
     */

    fun schedule(
        reminder: ReminderEntity
    ): ReminderScheduleResult {

        /*
         * No programamos fechas que ya pasaron.
         */
        if (
            reminder.triggerAtMillis <=
            System.currentTimeMillis()
        ) {

            return ReminderScheduleResult.InvalidTime
        }

        val pendingIntent =
            createPendingIntent(
                reminderId =
                    reminder.id,

                userId =
                    reminder.userId
            )

        return try {

            /*
             * Android 12 / API 31+
             *
             * canScheduleExactAlarms()
             * nos dice si podemos utilizar
             * una alarma exacta.
             */
            val canScheduleExact =
                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.S
                ) {

                    alarmManager
                        .canScheduleExactAlarms()

                } else {

                    true
                }

            if (
                canScheduleExact
            ) {

                /*
                 * Recordatorio lo más cercano
                 * posible al momento solicitado.
                 */
                alarmManager
                    .setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.triggerAtMillis,
                        pendingIntent
                    )

                ReminderScheduleResult
                    .ScheduledExact

            } else {

                /*
                 * Si Android no permite alarmas
                 * exactas, usamos una alarma
                 * aproximada.
                 *
                 * Así la app sigue funcionando
                 * sin lanzar SecurityException.
                 */
                alarmManager
                    .setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.triggerAtMillis,
                        pendingIntent
                    )

                ReminderScheduleResult
                    .ScheduledInexact
            }

        } catch (_: SecurityException) {

            /*
             * Protección adicional ante posibles
             * cambios de permiso entre la
             * comprobación y la programación.
             */
            try {

                alarmManager
                    .setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.triggerAtMillis,
                        pendingIntent
                    )

                ReminderScheduleResult
                    .ScheduledInexact

            } catch (_: Exception) {

                ReminderScheduleResult.Error
            }

        } catch (_: Exception) {

            ReminderScheduleResult.Error
        }
    }

    /*
     * =========================================================
     * CANCELAR RECORDATORIO
     * =========================================================
     */

    fun cancel(
        reminderId: Long,
        userId: Long
    ) {

        val pendingIntent =
            createPendingIntent(
                reminderId =
                    reminderId,

                userId =
                    userId
            )

        alarmManager.cancel(
            pendingIntent
        )

        pendingIntent.cancel()
    }

    /*
     * =========================================================
     * CREAR PENDING INTENT
     * =========================================================
     */

    private fun createPendingIntent(
        reminderId: Long,
        userId: Long
    ): PendingIntent {

        val intent =
            Intent(
                context,
                ReminderReceiver::class.java
            ).apply {

                putExtra(
                    ReminderReceiver
                        .EXTRA_REMINDER_ID,

                    reminderId
                )

                putExtra(
                    ReminderReceiver
                        .EXTRA_USER_ID,

                    userId
                )
            }

        return PendingIntent.getBroadcast(
            context,

            reminderId
                .toAlarmRequestCode(),

            intent,

            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }
}

/*
 * =============================================================
 * RESULTADO DE PROGRAMACIÓN
 * =============================================================
 */

sealed interface ReminderScheduleResult {

    /*
     * Android permitió programarlo
     * de manera exacta.
     */
    data object ScheduledExact :
        ReminderScheduleResult

    /*
     * Se programó correctamente,
     * pero Android puede retrasarlo
     * ligeramente.
     */
    data object ScheduledInexact :
        ReminderScheduleResult

    data object InvalidTime :
        ReminderScheduleResult

    data object Error :
        ReminderScheduleResult
}

/*
 * =============================================================
 * REQUEST CODE
 * =============================================================
 */

private fun Long.toAlarmRequestCode():
        Int {

    return (
            this xor
                    (this ushr 32)
            ).toInt()
}