package com.example.rachapro.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.rachapro.MainActivity
import com.example.rachapro.RachaProApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat


class ReminderReceiver :
    BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val reminderId =
            intent.getLongExtra(
                EXTRA_REMINDER_ID,
                -1L
            )

        val userId =
            intent.getLongExtra(
                EXTRA_USER_ID,
                -1L
            )

        if (
            reminderId <= 0L ||
            userId <= 0L
        ) {
            return
        }

        val pendingResult =
            goAsync()

        CoroutineScope(
            Dispatchers.IO
        ).launch {

            try {

                val application =
                    context.applicationContext
                            as RachaProApplication

                val session =
                    application
                        .sessionManager
                        .sessionState
                        .first()

                val activeUserId =
                    session.userId

                if (
                    activeUserId == null ||
                    activeUserId != userId
                ) {
                    return@launch
                }

                val reminder =
                    application
                        .reminderRepository
                        .getReminderById(
                            reminderId =
                                reminderId,

                            userId =
                                activeUserId
                        )

                if (
                    reminder == null ||
                    reminder.status !=
                    "SCHEDULED"
                ) {

                    return@launch
                }

                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return@launch
                }

                val openAppIntent =
                    Intent(
                        context,
                        MainActivity::class.java
                    ).apply {

                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }

                val contentPendingIntent =
                    PendingIntent.getActivity(
                        context,

                        reminderId
                            .toNotificationRequestCode(),

                        openAppIntent,

                        PendingIntent.FLAG_UPDATE_CURRENT or
                                PendingIntent.FLAG_IMMUTABLE
                    )

                val notification =
                    NotificationCompat.Builder(
                        context,
                        NotificationChannels
                            .REMINDERS_CHANNEL_ID
                    )
                        .setSmallIcon(
                            android.R.drawable.ic_dialog_info
                        )
                        .setContentTitle(
                            reminder.title
                        )

                        .setContentText(
                            reminder.message.ifBlank {
                                "Tienes un recordatorio pendiente."
                            }
                        )

                        .setContentIntent(
                            contentPendingIntent
                        )
                        .setAutoCancel(
                            true
                        )
                        .setCategory(
                            NotificationCompat
                                .CATEGORY_REMINDER
                        )
                        .build()

                try {

                    NotificationManagerCompat
                        .from(
                            context
                        )
                        .notify(
                            reminderId
                                .toNotificationRequestCode(),

                            notification
                        )

                } catch (_: SecurityException) {

                    return@launch
                }

                application
                    .reminderRepository
                    .markReminderDelivered(
                        reminderId =
                            reminderId,

                        userId =
                            userId
                    )

            } finally {

                pendingResult.finish()
            }
        }
    }

    companion object {

        const val EXTRA_REMINDER_ID =
            "extra_reminder_id"

        const val EXTRA_USER_ID =
            "extra_user_id"
    }
}

private fun Long.toNotificationRequestCode():
        Int {

    return (
            this xor
                    (this ushr 32)
            ).toInt()
}