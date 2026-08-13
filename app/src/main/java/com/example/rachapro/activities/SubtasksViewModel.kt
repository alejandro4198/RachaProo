package com.example.rachapro.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.rachapro.RachaProApplication
import com.example.rachapro.data.local.SessionManager
import com.example.rachapro.data.local.entity.SubtaskEntity
import com.example.rachapro.data.repository.SubtaskCreateResult
import com.example.rachapro.data.repository.SubtaskObserveResult
import com.example.rachapro.data.repository.SubtaskOperationResult
import com.example.rachapro.data.repository.SubtaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SubtasksViewModel(
    private val subtaskRepository: SubtaskRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<SubtasksUiState>(
            SubtasksUiState.Idle
        )

    val uiState: StateFlow<SubtasksUiState> =
        _uiState.asStateFlow()

    private val _actionState =
        MutableStateFlow<SubtaskActionState>(
            SubtaskActionState.Idle
        )

    val actionState: StateFlow<SubtaskActionState> =
        _actionState.asStateFlow()

    private var loadJob: Job? = null

    private var currentActivityId: Long? =
        null

    /*
     * ---------------------------------------------------------
     * CARGAR SUBTAREAS
     * ---------------------------------------------------------
     */

    fun loadSubtasks(
        activityId: Long
    ) {

        if (activityId <= 0) {

            _uiState.value =
                SubtasksUiState.Error(
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
                    SubtasksUiState.Loading

                try {

                    val session =
                        sessionManager
                            .sessionState
                            .first()

                    val userId =
                        session.userId

                    if (userId == null) {

                        _uiState.value =
                            SubtasksUiState.NoActiveSession

                        return@launch
                    }

                    when (
                        val result =
                            subtaskRepository
                                .observeSubtasks(
                                    userId = userId,
                                    activityId =
                                        activityId
                                )
                    ) {

                        is SubtaskObserveResult.Success -> {

                            result.subtasks
                                .collect { subtasks ->

                                    _uiState.value =
                                        SubtasksUiState.Success(
                                            userId =
                                                userId,

                                            activityId =
                                                activityId,

                                            subtasks =
                                                subtasks
                                        )
                                }
                        }

                        SubtaskObserveResult.NotFoundOrNotAllowed -> {

                            _uiState.value =
                                SubtasksUiState.Error(
                                    message =
                                        "La actividad no existe o no está disponible."
                                )
                        }

                        SubtaskObserveResult.Error -> {

                            _uiState.value =
                                SubtasksUiState.Error(
                                    message =
                                        "No fue posible cargar las subtareas."
                                )
                        }
                    }

                } catch (_: Exception) {

                    _uiState.value =
                        SubtasksUiState.Error(
                            message =
                                "Ocurrió un error al cargar las subtareas."
                        )
                }
            }
    }

    /*
     * ---------------------------------------------------------
     * CREAR
     * ---------------------------------------------------------
     */

    fun createSubtask(
        title: String
    ) {

        val currentState =
            _uiState.value

        if (
            currentState
                    !is SubtasksUiState.Success
        ) {

            _actionState.value =
                SubtaskActionState.Error(
                    message =
                        "No fue posible obtener la actividad."
                )

            return
        }

        if (
            _actionState.value
                    is SubtaskActionState.Creating
        ) {
            return
        }

        viewModelScope.launch {

            _actionState.value =
                SubtaskActionState.Creating

            when (
                val result =
                    subtaskRepository
                        .createSubtask(
                            userId =
                                currentState.userId,

                            activityId =
                                currentState.activityId,

                            title =
                                title
                        )
            ) {

                is SubtaskCreateResult.Success -> {

                    _actionState.value =
                        SubtaskActionState.CreateSuccess(
                            subtaskId =
                                result.subtaskId
                        )
                }

                is SubtaskCreateResult.InvalidData -> {

                    _actionState.value =
                        SubtaskActionState.ValidationError(
                            message =
                                result.message
                        )
                }

                SubtaskCreateResult.NotFoundOrNotAllowed -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "La actividad ya no está disponible."
                        )
                }

                SubtaskCreateResult.Error -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "No fue posible crear la subtarea."
                        )
                }
            }
        }
    }

    /*
     * ---------------------------------------------------------
     * EDITAR
     * ---------------------------------------------------------
     */

    fun updateSubtask(
        subtaskId: Long,
        title: String
    ) {

        val currentState =
            _uiState.value

        if (
            currentState
                    !is SubtasksUiState.Success
        ) {
            return
        }

        viewModelScope.launch {

            _actionState.value =
                SubtaskActionState.Updating(
                    subtaskId =
                        subtaskId
                )

            when (
                subtaskRepository
                    .updateSubtaskTitle(
                        userId =
                            currentState.userId,

                        activityId =
                            currentState.activityId,

                        subtaskId =
                            subtaskId,

                        title =
                            title
                    )
            ) {

                SubtaskOperationResult.Success -> {

                    _actionState.value =
                        SubtaskActionState.UpdateSuccess(
                            subtaskId =
                                subtaskId
                        )
                }

                SubtaskOperationResult.InvalidData -> {

                    _actionState.value =
                        SubtaskActionState.ValidationError(
                            message =
                                "El nombre de la subtarea no puede estar vacío."
                        )
                }

                SubtaskOperationResult.NotFoundOrNotAllowed -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "La subtarea ya no está disponible."
                        )
                }

                SubtaskOperationResult.Error -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "No fue posible editar la subtarea."
                        )
                }
            }
        }
    }

    /*
     * ---------------------------------------------------------
     * COMPLETAR / DESMARCAR
     * ---------------------------------------------------------
     */

    fun setSubtaskCompleted(
        subtaskId: Long,
        isCompleted: Boolean
    ) {

        val currentState =
            _uiState.value

        if (
            currentState
                    !is SubtasksUiState.Success
        ) {
            return
        }

        viewModelScope.launch {

            _actionState.value =
                SubtaskActionState.ChangingCompletion(
                    subtaskId =
                        subtaskId
                )

            when (
                subtaskRepository
                    .setSubtaskCompleted(
                        userId =
                            currentState.userId,

                        activityId =
                            currentState.activityId,

                        subtaskId =
                            subtaskId,

                        isCompleted =
                            isCompleted
                    )
            ) {

                SubtaskOperationResult.Success -> {

                    _actionState.value =
                        SubtaskActionState.CompletionSuccess(
                            subtaskId =
                                subtaskId
                        )
                }

                SubtaskOperationResult.NotFoundOrNotAllowed -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "La subtarea ya no está disponible."
                        )
                }

                SubtaskOperationResult.InvalidData -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "No fue posible cambiar el estado."
                        )
                }

                SubtaskOperationResult.Error -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "No fue posible cambiar el estado de la subtarea."
                        )
                }
            }
        }
    }

    /*
     * ---------------------------------------------------------
     * ELIMINAR
     * ---------------------------------------------------------
     */

    fun deleteSubtask(
        subtaskId: Long
    ) {

        val currentState =
            _uiState.value

        if (
            currentState
                    !is SubtasksUiState.Success
        ) {
            return
        }

        viewModelScope.launch {

            _actionState.value =
                SubtaskActionState.Deleting(
                    subtaskId =
                        subtaskId
                )

            when (
                subtaskRepository
                    .deleteSubtask(
                        userId =
                            currentState.userId,

                        activityId =
                            currentState.activityId,

                        subtaskId =
                            subtaskId
                    )
            ) {

                SubtaskOperationResult.Success -> {

                    _actionState.value =
                        SubtaskActionState.DeleteSuccess(
                            subtaskId =
                                subtaskId
                        )
                }

                SubtaskOperationResult.NotFoundOrNotAllowed -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "La subtarea ya no está disponible."
                        )
                }

                SubtaskOperationResult.InvalidData -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "No fue posible eliminar la subtarea."
                        )
                }

                SubtaskOperationResult.Error -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "Ocurrió un error al eliminar la subtarea."
                        )
                }
            }
        }
    }

    fun resetActionState() {

        _actionState.value =
            SubtaskActionState.Idle
    }

    /*
     * ---------------------------------------------------------
     * FACTORY
     * ---------------------------------------------------------
     */

    companion object {

        val Factory:
                ViewModelProvider.Factory =
            viewModelFactory {

                initializer {

                    val application =
                        this[APPLICATION_KEY]
                                as RachaProApplication

                    SubtasksViewModel(
                        subtaskRepository =
                            application.subtaskRepository,

                        sessionManager =
                            application.sessionManager
                    )
                }
            }
    }
}

/*
 * =============================================================
 * ESTADO DE LA PANTALLA
 * =============================================================
 */

sealed interface SubtasksUiState {

    data object Idle :
        SubtasksUiState

    data object Loading :
        SubtasksUiState

    data class Success(
        val userId: Long,
        val activityId: Long,
        val subtasks:
        List<SubtaskEntity>
    ) : SubtasksUiState

    data object NoActiveSession :
        SubtasksUiState

    data class Error(
        val message: String
    ) : SubtasksUiState
}

/*
 * =============================================================
 * ESTADO DE OPERACIONES
 * =============================================================
 */

sealed interface SubtaskActionState {

    data object Idle :
        SubtaskActionState

    data object Creating :
        SubtaskActionState

    data class CreateSuccess(
        val subtaskId: Long
    ) : SubtaskActionState

    data class Updating(
        val subtaskId: Long
    ) : SubtaskActionState

    data class UpdateSuccess(
        val subtaskId: Long
    ) : SubtaskActionState

    data class ChangingCompletion(
        val subtaskId: Long
    ) : SubtaskActionState

    data class CompletionSuccess(
        val subtaskId: Long
    ) : SubtaskActionState

    data class Deleting(
        val subtaskId: Long
    ) : SubtaskActionState

    data class DeleteSuccess(
        val subtaskId: Long
    ) : SubtaskActionState

    data class ValidationError(
        val message: String
    ) : SubtaskActionState

    data class Error(
        val message: String
    ) : SubtaskActionState
}