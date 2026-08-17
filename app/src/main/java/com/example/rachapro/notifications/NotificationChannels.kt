package com.example.rachapro.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {

    const val REMINDERS_CHANNEL_ID =
        "racha_pro_reminders"

    private const val REMINDERS_CHANNEL_NAME =
        "Recordatorios"

    private const val REMINDERS_CHANNEL_DESCRIPTION =
        "Recordatorios de actividades y tareas de RachaPro"

    fun createNotificationChannels(
        context: Context
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val reminderChannel =
                NotificationChannel(
                    REMINDERS_CHANNEL_ID,
                    REMINDERS_CHANNEL_NAME,
                    NotificationManager
                        .IMPORTANCE_DEFAULT
                ).apply {

                    description =
                        REMINDERS_CHANNEL_DESCRIPTION
                }

            val notificationManager =
                context.getSystemService(
                    NotificationManager::class.java
                )

            notificationManager
                .createNotificationChannel(
                    reminderChannel
                )
        }
    }
}