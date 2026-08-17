package com.example.rachapro.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.rachapro.RachaProApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver :
    BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        if (
            intent.action !=
            Intent.ACTION_BOOT_COMPLETED
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

                val userId =
                    session.userId

                if (
                    userId == null
                ) {
                    return@launch
                }

                val reminders =
                    application
                        .reminderRepository
                        .getScheduledReminders(
                            userId = userId
                        )

                val currentTime =
                    System.currentTimeMillis()

                reminders.forEach { reminder ->

                    if (
                        reminder.triggerAtMillis >
                        currentTime
                    ) {

                        application
                            .reminderScheduler
                            .schedule(
                                reminder = reminder
                            )

                    } else {

                        application
                            .reminderRepository
                            .cancelReminder(
                                reminderId =
                                    reminder.id,

                                userId =
                                    userId
                            )
                    }
                }

            } finally {

                pendingResult.finish()
            }
        }
    }
}