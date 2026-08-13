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

        /*
         * Solo reaccionamos al arranque completo
         * del dispositivo.
         */
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

                /*
                 * Recuperamos la sesión persistida.
                 *
                 * Si no existe usuario autenticado,
                 * no restauramos recordatorios.
                 */
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

                /*
                 * Room sigue conservando los
                 * recordatorios después del reinicio.
                 */
                val reminders =
                    application
                        .reminderRepository
                        .getScheduledReminders(
                            userId = userId
                        )

                val currentTime =
                    System.currentTimeMillis()

                reminders.forEach { reminder ->

                    /*
                     * Solo restauramos alarmas
                     * cuya fecha todavía está
                     * en el futuro.
                     */
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

                        /*
                         * Si el momento ya pasó,
                         * no queremos dejar un
                         * SCHEDULED fantasma.
                         *
                         * Más adelante podemos
                         * crear un estado MISSED.
                         */
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