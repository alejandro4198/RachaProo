package com.example.rachapro.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.rachapro.RachaProApplication
import com.example.rachapro.data.local.SessionManager
import com.example.rachapro.data.local.entity.PomodoroSessionEntity
import com.example.rachapro.data.local.entity.PomodoroSessionStatus
import com.example.rachapro.data.repository.PomodoroCompleteResult
import com.example.rachapro.data.repository.PomodoroRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.rachapro.data.local.PomodoroPreferences
import com.example.rachapro.data.local.UserPreferencesManager
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.rachapro.data.local.entity.PomodoroSessionType
import com.example.rachapro.data.repository.PomodoroOperationResult
import com.example.rachapro.data.repository.PomodoroStartResult


class PomodoroViewModel(
    private val pomodoroRepository: PomodoroRepository,
    private val sessionManager: SessionManager,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PomodoroUiState>(
            PomodoroUiState.Loading
        )

    val uiState: StateFlow<PomodoroUiState> =
        _uiState.asStateFlow()

    private val _actionState =
        MutableStateFlow<PomodoroActionState>(
            PomodoroActionState.Idle
        )

    val actionState: StateFlow<PomodoroActionState> =
        _actionState.asStateFlow()

    private var timerJob: Job? = null

    private var currentUserId: Long? = null

    private var completingSessionId: Long? = null

    private var pomodoroPreferences =
        PomodoroPreferences()

    init {
        loadPomodoro()
    }

    private fun loadPomodoro() {

        viewModelScope.launch {

            val session =
                sessionManager
                    .sessionState
                    .first()

            val userId =
                session.userId

            if (userId == null) {

                _uiState.value =
                    PomodoroUiState.NoActiveSession

                return@launch
            }

            currentUserId =
                userId

            pomodoroRepository.syncRemoteSessions(
                userId = userId
            )

            combine(
                userPreferencesManager
                    .observePomodoroPreferences(
                        userId = userId
                    ),

                pomodoroRepository
                    .observeActiveSession(
                        userId = userId
                    )
            ) { preferences, activeSession ->

                pomodoroPreferences = preferences

                if (activeSession == null) {

                    timerJob?.cancel()

                    PomodoroUiState.Idle(
                        preferences = preferences
                    )

                } else {

                    startTimer(
                        session = activeSession,
                        preferences = preferences
                    )

                    null
                }

            }.collect { idleState ->

                if (idleState != null) {

                    val current = _uiState.value

                    if (
                        current is PomodoroUiState.Completed ||
                        current is PomodoroUiState.Error
                    ) {
                        return@collect
                    }

                    _uiState.value = idleState
                }
            }
        }
    }

    fun startFocus(
        activityId: Long? = null
    ) {
        startSession(
            type = PomodoroSessionType.FOCUS,
            durationMinutes = pomodoroPreferences.focusMinutes,
            activityId = activityId
        )
    }

    fun startShortBreak() {
        startSession(
            type = PomodoroSessionType.SHORT_BREAK,
            durationMinutes = pomodoroPreferences.shortBreakMinutes
        )
    }

    fun startLongBreak() {
        startSession(
            type = PomodoroSessionType.LONG_BREAK,
            durationMinutes = pomodoroPreferences.longBreakMinutes
        )
    }

    private fun startSession(
        type: String,
        durationMinutes: Int,
        activityId: Long? = null
    ) {

        val userId =
            currentUserId
                ?: return

        val canStart =
            _uiState.value is PomodoroUiState.Idle ||
                    _uiState.value is PomodoroUiState.Completed

        if (!canStart) {
            return
        }

        if (durationMinutes <= 0) {

            _actionState.value =
                PomodoroActionState.Error(
                    "La duración no es válida."
                )

            return
        }

        viewModelScope.launch {

            _actionState.value =
                PomodoroActionState.Starting

            when (
                val result =
                    pomodoroRepository.startSession(
                        userId = userId,
                        activityId = activityId,
                        type = type,
                        plannedDurationSeconds =
                            durationMinutes * 60
                    )
            ) {

                is PomodoroStartResult.Success -> {

                    _actionState.value =
                        PomodoroActionState.Idle
                }

                is PomodoroStartResult.InvalidData -> {

                    _actionState.value =
                        PomodoroActionState.Error(
                            result.message
                        )
                }

                PomodoroStartResult.ActiveSessionAlreadyExists -> {

                    _actionState.value =
                        PomodoroActionState.Error(
                            "Ya tienes una sesión activa."
                        )
                }

                PomodoroStartResult.ActivityNotFoundOrNotAllowed -> {

                    _actionState.value =
                        PomodoroActionState.Error(
                            "La actividad seleccionada no está disponible."
                        )
                }

                PomodoroStartResult.Error -> {

                    _actionState.value =
                        PomodoroActionState.Error(
                            "No fue posible iniciar la sesión."
                        )
                }
            }
        }
    }

    fun pause() {

        val userId =
            currentUserId
                ?: return

        val state =
            _uiState.value

        if (
            state !is PomodoroUiState.Active ||
            state.session.status !=
            PomodoroSessionStatus.RUNNING
        ) {
            return
        }

        viewModelScope.launch {

            _actionState.value =
                PomodoroActionState.Pausing

            when (
                pomodoroRepository.pauseSession(
                    sessionId = state.session.id,
                    userId = userId
                )
            ) {

                PomodoroOperationResult.Success -> {

                    _actionState.value =
                        PomodoroActionState.Idle
                }

                PomodoroOperationResult.NotFoundOrInvalidState -> {

                    _actionState.value =
                        PomodoroActionState.Error(
                            "No fue posible pausar esta sesión."
                        )
                }

                PomodoroOperationResult.Error -> {

                    _actionState.value =
                        PomodoroActionState.Error(
                            "Ocurrió un error al pausar el Pomodoro."
                        )
                }
            }
        }
    }

    fun resume() {

        val userId =
            currentUserId
                ?: return

        val state =
            _uiState.value

        if (
            state !is PomodoroUiState.Active ||
            state.session.status !=
            PomodoroSessionStatus.PAUSED
        ) {
            return
        }

        viewModelScope.launch {

            _actionState.value =
                PomodoroActionState.Resuming

            when (
                pomodoroRepository.resumeSession(
                    sessionId = state.session.id,
                    userId = userId
                )
            ) {

                PomodoroOperationResult.Success -> {

                    _actionState.value =
                        PomodoroActionState.Idle
                }

                PomodoroOperationResult.NotFoundOrInvalidState -> {

                    _actionState.value =
                        PomodoroActionState.Error(
                            "No fue posible reanudar esta sesión."
                        )
                }

                PomodoroOperationResult.Error -> {

                    _actionState.value =
                        PomodoroActionState.Error(
                            "Ocurrió un error al reanudar el Pomodoro."
                        )
                }
            }
        }
    }

    fun cancel() {

        val userId =
            currentUserId
                ?: return

        val state =
            _uiState.value

        if (state !is PomodoroUiState.Active) {
            return
        }

        viewModelScope.launch {

            _actionState.value =
                PomodoroActionState.Cancelling

            when (
                pomodoroRepository.cancelSession(
                    sessionId = state.session.id,
                    userId = userId
                )
            ) {

                PomodoroOperationResult.Success -> {

                    _actionState.value =
                        PomodoroActionState.Idle
                }

                PomodoroOperationResult.NotFoundOrInvalidState -> {

                    _actionState.value =
                        PomodoroActionState.Error(
                            "La sesión ya no está disponible."
                        )
                }

                PomodoroOperationResult.Error -> {

                    _actionState.value =
                        PomodoroActionState.Error(
                            "No fue posible cancelar el Pomodoro."
                        )
                }
            }
        }
    }

    fun resetActionState() {

        _actionState.value =
            PomodoroActionState.Idle
    }

    fun dismissCompleted() {

        _uiState.value =
            PomodoroUiState.Idle(
                preferences = pomodoroPreferences
            )
    }

    private fun startTimer(
        session: PomodoroSessionEntity,
        preferences: PomodoroPreferences
    ) {

        timerJob?.cancel()

        timerJob =
            viewModelScope.launch {

                while (true) {

                    val remainingMillis =
                        pomodoroRepository
                            .calculateRemainingMillis(
                                session = session
                            )

                    _uiState.value =
                        PomodoroUiState.Active(
                            session = session,
                            remainingMillis =
                                remainingMillis,
                            preferences = preferences
                        )

                    if (
                        remainingMillis <= 0L &&
                        session.status ==
                        PomodoroSessionStatus.RUNNING
                    ) {

                        completeAutomatically(
                            session = session
                        )

                        break
                    }

                    delay(1000L)
                }
            }
    }

    private suspend fun completeAutomatically(
        session: PomodoroSessionEntity
    ) {

        val userId =
            currentUserId
                ?: return

        if (
            completingSessionId ==
            session.id
        ) {
            return
        }

        completingSessionId =
            session.id

        when (
            pomodoroRepository
                .completeSession(
                    sessionId = session.id,
                    userId = userId
                )
        ) {

            PomodoroCompleteResult.Success -> {

                val completedFocusCount =
                    if (
                        session.type ==
                        PomodoroSessionType.FOCUS
                    ) {

                        pomodoroRepository
                            .countCompletedFocusSessions(
                                userId = userId
                            )

                    } else {

                        null
                    }

                val recommendedType =
                    if (completedFocusCount != null) {

                        if (
                            completedFocusCount > 0 &&
                            completedFocusCount % 4 == 0
                        ) {

                            PomodoroSessionType.LONG_BREAK

                        } else {

                            PomodoroSessionType.SHORT_BREAK
                        }

                    } else {

                        null
                    }

                _uiState.value =
                    PomodoroUiState.Completed(
                        sessionId = session.id,
                        recommendedType = recommendedType,
                        completedFocusCount = completedFocusCount,
                        preferences = pomodoroPreferences
                    )
            }

            PomodoroCompleteResult.TimeRemaining -> Unit

            PomodoroCompleteResult.InvalidState -> Unit

            PomodoroCompleteResult.NotFoundOrNotAllowed -> {

                _uiState.value =
                    PomodoroUiState.Error(
                        message =
                            "La sesión ya no está disponible."
                    )
            }

            PomodoroCompleteResult.Error -> {

                _uiState.value =
                    PomodoroUiState.Error(
                        message =
                            "No fue posible completar el Pomodoro."
                    )
            }
        }

        completingSessionId =
            null
    }

    companion object {

        val Factory:
                ViewModelProvider.Factory =
            viewModelFactory {

                initializer {

                    val application =
                        this[APPLICATION_KEY]
                                as RachaProApplication

                    PomodoroViewModel(
                        pomodoroRepository =
                            application.pomodoroRepository,

                        sessionManager =
                            application.sessionManager,

                        userPreferencesManager =
                            application.userPreferencesManager
                    )
                }
            }
    }
}

sealed interface PomodoroUiState {

    data object Loading :
        PomodoroUiState

    data class Idle(
        val preferences: PomodoroPreferences =
            PomodoroPreferences()
    ) : PomodoroUiState

    data object NoActiveSession :
        PomodoroUiState

    data class Active(
        val session: PomodoroSessionEntity,
        val remainingMillis: Long,
        val preferences: PomodoroPreferences =
            PomodoroPreferences()
    ) : PomodoroUiState

    data class Completed(
        val sessionId: Long,
        val recommendedType: String? = null,
        val completedFocusCount: Int? = null,
        val preferences: PomodoroPreferences =
            PomodoroPreferences()
    ) : PomodoroUiState

    data class Error(
        val message: String
    ) : PomodoroUiState
}

sealed interface PomodoroActionState {

    data object Idle :
        PomodoroActionState

    data object Starting :
        PomodoroActionState

    data object Pausing :
        PomodoroActionState

    data object Resuming :
        PomodoroActionState

    data object Cancelling :
        PomodoroActionState

    data class Error(
        val message: String
    ) : PomodoroActionState
}