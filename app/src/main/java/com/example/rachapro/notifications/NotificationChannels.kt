package com.example.rachapro.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {

    /*
     * ID interno y estable del canal.
     *
     * Este valor NO debe cambiar después,
     * porque Android identifica el canal
     * mediante este String.
     */
    const val REMINDERS_CHANNEL_ID =
        "racha_pro_reminders"

    private const val REMINDERS_CHANNEL_NAME =
        "Recordatorios"

    private const val REMINDERS_CHANNEL_DESCRIPTION =
        "Recordatorios de actividades y tareas de RachaPro"

    fun createNotificationChannels(
        context: Context
    ) {

        /*
         * Los NotificationChannel existen
         * desde Android 8.0 / API 26.
         */
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