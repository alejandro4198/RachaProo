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
import com.example.rachapro.data.repository.RemoteReminderCreateResult
import com.example.rachapro.data.repository.RemoteReminderOperationResult
import com.example.rachapro.data.repository.RemoteRemindersResult
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

    private var loadJob: Job? = null

    private var currentActivityId: Long? = null

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

                    when (
                        val result =
                            reminderRepository
                                .fetchRemoteReminders(
                                    userId = userId,
                                    activityId = activityId
                                )
                    ) {

                        is RemoteRemindersResult.Success -> {

                            _uiState.value =
                                ReminderUiState.Success(
                                    userId = userId,
                                    activityId = activityId,
                                    reminders =
                                        result.reminders
                                )
                        }

                        RemoteRemindersResult.Unauthorized -> {

                            _uiState.value =
                                ReminderUiState.NoActiveSession
                        }

                        RemoteRemindersResult.Error -> {

                            _uiState.value =
                                ReminderUiState.Error(
                                    message =
                                        "No fue posible cargar los recordatorios."
                                )
                        }
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

    fun retry() {

        val activityId =
            currentActivityId
                ?: return

        loadReminders(
            activityId = activityId
        )
    }

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
                        .createRemoteReminder(
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

                is RemoteReminderCreateResult.Success -> {

                    scheduleCreatedReminder(
                        reminder =
                            result.reminder,

                        userId =
                            currentState.userId
                    )
                }

                is RemoteReminderCreateResult.InvalidData -> {

                    _actionState.value =
                        ReminderActionState.ValidationError(
                            message =
                                result.message
                        )
                }

                RemoteReminderCreateResult
                    .ActivityNotFoundOrNotAllowed -> {

                    _actionState.value =
                        ReminderActionState.Error(
                            message =
                                "La actividad ya no está disponible."
                        )
                }

                RemoteReminderCreateResult.Unauthorized -> {

                    _actionState.value =
                        ReminderActionState.Error(
                            message =
                                "La sesión expiró. Inicia sesión nuevamente."
                        )
                }

                RemoteReminderCreateResult.Error -> {

                    _actionState.value =
                        ReminderActionState.Error(
                            message =
                                "No fue posible crear el recordatorio."
                        )
                }
            }
        }
    }

    private suspend fun scheduleCreatedReminder(
        reminder: ReminderEntity,
        userId: Long
    ) {

        when (
            reminderScheduler.schedule(
                reminder = reminder
            )
        ) {

            ReminderScheduleResult.ScheduledExact -> {

                _actionState.value =
                    ReminderActionState.CreateSuccess(
                        reminderId =
                            reminder.id,

                        isExact =
                            true
                    )

                currentActivityId?.let { activityId ->

                    loadReminders(
                        activityId = activityId
                    )
                }
            }

            ReminderScheduleResult.ScheduledInexact -> {

                _actionState.value =
                    ReminderActionState.CreateSuccess(
                        reminderId =
                            reminder.id,

                        isExact =
                            false
                    )

                currentActivityId?.let { activityId ->

                    loadReminders(
                        activityId = activityId
                    )
                }
            }

            ReminderScheduleResult.InvalidTime -> {

                compensateFailedSchedule(
                    reminderId =
                        reminder.id,

                    userId =
                        userId
                )

                _actionState.value =
                    ReminderActionState.ValidationError(
                        message =
                            "Selecciona una fecha y hora futuras."
                    )
            }

            ReminderScheduleResult.Error -> {

                compensateFailedSchedule(
                    reminderId =
                        reminder.id,

                    userId =
                        userId
                )

                _actionState.value =
                    ReminderActionState.Error(
                        message =
                            "El recordatorio se guardó, pero Android no pudo programarlo."
                    )
            }
        }
    }

    private suspend fun compensateFailedSchedule(
        reminderId: Long,
        userId: Long
    ) {

        try {

            reminderRepository
                .cancelRemoteReminder(
                    reminderId =
                        reminderId,

                    userId =
                        userId
                )

        } catch (_: Exception) {
        }

        try {

            reminderRepository
                .cancelReminder(
                    reminderId =
                        reminderId,

                    userId =
                        userId
                )

        } catch (_: Exception) {
        }
    }

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

            when (
                reminderRepository
                    .cancelRemoteReminder(
                        reminderId =
                            reminderId,

                        userId =
                            currentState.userId
                    )
            ) {

                is RemoteReminderOperationResult.Success -> {

                    try {

                        reminderScheduler.cancel(
                            reminderId =
                                reminderId,

                            userId =
                                currentState.userId
                        )

                    } catch (_: Exception) {
                    }

                    _actionState.value =
                        ReminderActionState.CancelSuccess(
                            reminderId =
                                reminderId
                        )

                    loadReminders(
                        activityId =
                            currentState.activityId
                    )
                }

                RemoteReminderOperationResult.NotFound -> {

                    _actionState.value =
                        ReminderActionState.Error(
                            message =
                                "El recordatorio ya no está disponible."
                        )
                }

                RemoteReminderOperationResult.Unauthorized -> {

                    _actionState.value =
                        ReminderActionState.Error(
                            message =
                                "La sesión expiró. Inicia sesión nuevamente."
                        )
                }

                RemoteReminderOperationResult.Error -> {

                    _actionState.value =
                        ReminderActionState.Error(
                            message =
                                "No fue posible cancelar el recordatorio."
                        )
                }
            }
        }
    }

    fun resetActionState() {

        _actionState.value =
            ReminderActionState.Idle
    }

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

sealed interface ReminderActionState {

    data object Idle :
        ReminderActionState

    data object Creating :
        ReminderActionState

    data class CreateSuccess(
        val reminderId: Long,
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