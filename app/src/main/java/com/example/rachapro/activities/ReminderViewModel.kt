package com.example.rachapro.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.rachapro.RachaProApplication
import com.example.rachapro.data.local.SessionManager
import com.example.rachapro.data.local.entity.ReminderEntity
import com.example.rachapro.data.repository.ReminderCreateResult
import com.example.rachapro.data.repository.ReminderOperationResult
import com.example.rachapro.data.repository.ReminderRepository
import com.example.rachapro.notifications.ReminderScheduleResult
import com.example.rachapro.notifications.ReminderScheduler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ReminderUiState>(
            ReminderUiState.Idle
        )

    val uiState: StateFlow<ReminderUiState> =
        _uiState.asStateFlow()

    private val _actionState =
        MutableStateFlow<ReminderActionState>(
            ReminderActionState.Idle
        )

    val actionState: StateFlow<ReminderActionState> =
        _actionState.asStateFlow()

    private var loadJob: Job? =
        null

    private var currentActivityId: Long? =
        null

    /*
     * =========================================================
     * CARGAR RECORDATORIOS DE UNA ACTIVIDAD
     * =========================================================
     */

    fun loadReminders(
        activityId: Long
    ) {

        if (activityId <= 0L) {

            _uiState.value =
                ReminderUiState.Error(
                    message =
                        "La actividad no es válida."
                )

            return
        }

        currentActivityId =
            activityId

        loadJob?.cancel()

        loadJob =
            viewModelScope.launch {

                _uiState.value =
                    ReminderUiState.Loading

                try {

                    val session =
                        sessionManager
                            .sessionState
                            .first()

                    val userId =
                        session.userId

                    if (userId == null) {

                        _uiState.value =
                            ReminderUiState.NoActiveSession

                        return@launch
                    }

                    reminderRepository
                        .observeRemindersByActivity(
                            userId = userId,
                            activityId = activityId
                        )
                        .collect { reminders ->

                            _uiState.value =
                                ReminderUiState.Success(
                                    userId = userId,
                                    activityId = activityId,
                                    reminders = reminders
                                )
                        }

                } catch (_: Exception) {

                    _uiState.value =
                        ReminderUiState.Error(
                            message =
                                "No fue posible cargar los recordatorios."
                        )
                }
            }
    }

    /*
     * =========================================================
     * REINTENTAR
     * =========================================================
     */

    fun retry() {

        val activityId =
            currentActivityId
                ?: return

        loadReminders(
            activityId = activityId
        )
    }

    /*
     * =========================================================
     * CREAR Y PROGRAMAR
     * =========================================================
     */

    fun createReminder(
        title: String,
        message: String,
        triggerAtMillis: Long
    ) {

        if (
            _actionState.value
                    is ReminderActionState.Creating
        ) {
            return
        }

        val currentState =
            _uiState.value

        if (
            currentState
                    !is ReminderUiState.Success
        ) {

            _actionState.value =
                ReminderActionState.Error(
                    message =
                        "No fue posible obtener la actividad."
                )

            return
        }

        viewModelScope.launch {

            _actionState.value =
                ReminderActionState.Creating

            when (
                val result =
                    reminderRepository
                        .createReminder(
                            userId =
                                currentState.userId,

                            activityId =
                                currentState.activityId,

                            title =
                                title,

                            message =
                                message,

                            triggerAtMillis =
                                triggerAtMillis
                        )
            ) {

                is ReminderCreateResult.Success -> {

                    scheduleCreatedReminder(
                        reminderId =
                            result.reminderId,

                        userId =
                            currentState.userId
                    )
                }

                is ReminderCreateResult.InvalidData -> {

                    _actionState.value =
                        ReminderActionState.ValidationError(
                            message =
                                result.message
                        )
                }

                ReminderCreateResult
                    .ActivityNotFoundOrNotAllowed -> {

                    _actionState.value =
                        ReminderActionState.Error(
                            message =
                                "La actividad ya no está disponible."
                        )
                }

                ReminderCreateResult.Error -> {

                    _actionState.value =
                        ReminderActionState.Error(
                            message =
                                "No fue posible crear el recordatorio."
                        )
                }
            }
        }
    }

    /*
     * =========================================================
     * PROGRAMAR DESPUÉS DE INSERTAR EN ROOM
     * =========================================================
     */

    private suspend fun scheduleCreatedReminder(
        reminderId: Long,
        userId: Long
    ) {

        val reminder =
            reminderRepository
                .getReminderById(
                    reminderId = reminderId,
                    userId = userId
                )

        /*
         * Si por alguna razón Room no puede devolver
         * el registro recién creado, no continuamos.
         */
        if (reminder == null) {

            _actionState.value =
                ReminderActionState.Error(
                    message =
                        "No fue posible programar el recordatorio."
                )

            return
        }

        when (
            reminderScheduler.schedule(
                reminder = reminder
            )
        ) {

            ReminderScheduleResult
                .ScheduledExact -> {

                _actionState.value =
                    ReminderActionState.CreateSuccess(
                        reminderId = reminder.id,
                        isExact = true
                    )
            }

            ReminderScheduleResult
                .ScheduledInexact -> {

                _actionState.value =
                    ReminderActionState.CreateSuccess(
                        reminderId = reminder.id,
                        isExact = false
                    )
            }

            ReminderScheduleResult
                .InvalidTime -> {

                /*
                 * El registro existe en Room pero no
                 * debe quedar como SCHEDULED.
                 */
                reminderRepository
                    .cancelReminder(
                        reminderId = reminder.id,
                        userId = userId
                    )

                _actionState.value =
                    ReminderActionState.ValidationError(
                        message =
                            "Selecciona una fecha y hora futuras."
                    )
            }

            ReminderScheduleResult.Error -> {

                /*
                 * Compensación:
                 *
                 * si AlarmManager falló,
                 * cancelamos también el registro lógico.
                 */
                reminderRepository
                    .cancelReminder(
                        reminderId = reminder.id,
                        userId = userId
                    )

                _actionState.value =
                    ReminderActionState.Error(
                        message =
                            "El recordatorio se guardó, pero Android no pudo programarlo."
                    )
            }
        }
    }

    /*
     * =========================================================
     * CANCELAR
     * =========================================================
     */

    fun cancelReminder(
        reminderId: Long
    ) {

        if (
            _actionState.value
                    is ReminderActionState.Cancelling
        ) {
            return
        }

        val currentState =
            _uiState.value

        if (
            currentState
                    !is ReminderUiState.Success
        ) {
            return
        }

        viewModelScope.launch {

            _actionState.value =
                ReminderActionState.Cancelling(
                    reminderId =
                        reminderId
                )

            /*
             * Primero cancelamos en Room.
             *
             * Esto es importante porque incluso si
             * AlarmManager conservara accidentalmente
             * el PendingIntent, ReminderReceiver revisa
             * que el estado siga siendo SCHEDULED.
             */
            when (
                reminderRepository
                    .cancelReminder(
                        reminderId =
                            reminderId,

                        userId =
                            currentState.userId
                    )
            ) {

                ReminderOperationResult.Success -> {

                    try {

                        reminderScheduler.cancel(
                            reminderId =
                                reminderId,

                            userId =
                                currentState.userId
                        )

                    } catch (_: Exception) {

                        /*
                         * Room ya está CANCELLED.
                         *
                         * Aunque AlarmManager intentara
                         * ejecutar después el receiver,
                         * ReminderReceiver no mostrará
                         * una notificación cancelada.
                         */
                    }

                    _actionState.value =
                        ReminderActionState.CancelSuccess(
                            reminderId =
                                reminderId
                        )
                }

                ReminderOperationResult
                    .NotFoundOrNotAllowed -> {

                    _actionState.value =
                        ReminderActionState.Error(
                            message =
                                "El recordatorio ya no está disponible."
                        )
                }

                ReminderOperationResult.Error -> {

                    _actionState.value =
                        ReminderActionState.Error(
                            message =
                                "No fue posible cancelar el recordatorio."
                        )
                }
            }
        }
    }

    /*
     * =========================================================
     * LIMPIAR ESTADO DE OPERACIÓN
     * =========================================================
     */

    fun resetActionState() {

        _actionState.value =
            ReminderActionState.Idle
    }

    /*
     * =========================================================
     * FACTORY
     * =========================================================
     */

    companion object {

        val Factory:
                ViewModelProvider.Factory =
            viewModelFactory {

                initializer {

                    val application =
                        this[APPLICATION_KEY]
                                as RachaProApplication

                    ReminderViewModel(
                        reminderRepository =
                            application.reminderRepository,

                        reminderScheduler =
                            application.reminderScheduler,

                        sessionManager =
                            application.sessionManager
                    )
                }
            }
    }
}

/*
 * =============================================================
 * ESTADO PRINCIPAL
 * =============================================================
 */

sealed interface ReminderUiState {

    data object Idle :
        ReminderUiState

    data object Loading :
        ReminderUiState

    data class Success(
        val userId: Long,
        val activityId: Long,
        val reminders:
        List<ReminderEntity>
    ) : ReminderUiState

    data object NoActiveSession :
        ReminderUiState

    data class Error(
        val message: String
    ) : ReminderUiState
}

/*
 * =============================================================
 * ESTADOS DE OPERACIONES
 * =============================================================
 */

sealed interface ReminderActionState {

    data object Idle :
        ReminderActionState

    data object Creating :
        ReminderActionState

    data class CreateSuccess(
        val reminderId: Long,

        /*
         * true  → alarma exacta
         * false → Android la programó de manera aproximada
         */
        val isExact: Boolean
    ) : ReminderActionState

    data class Cancelling(
        val reminderId: Long
    ) : ReminderActionState

    data class CancelSuccess(
        val reminderId: Long
    ) : ReminderActionState

    data class ValidationError(
        val message: String
    ) : ReminderActionState

    data class Error(
        val message: String
    ) : ReminderActionState
}