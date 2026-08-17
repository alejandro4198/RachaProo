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

    fun schedule(
        reminder: ReminderEntity
    ): ReminderScheduleResult {

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

                alarmManager
                    .setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.triggerAtMillis,
                        pendingIntent
                    )

                ReminderScheduleResult
                    .ScheduledExact

            } else {

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


sealed interface ReminderScheduleResult {

    /*
     * Android permitió programarlo
     * de manera exacta.
     */
    data object ScheduledExact :
        ReminderScheduleResult

    data object ScheduledInexact :
        ReminderScheduleResult

    data object InvalidTime :
        ReminderScheduleResult

    data object Error :
        ReminderScheduleResult
}

private fun Long.toAlarmRequestCode():
        Int {

    return (
            this xor
                    (this ushr 32)
            ).toInt()
}