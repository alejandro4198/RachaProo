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
import com.example.rachapro.data.repository.SubtaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.rachapro.data.repository.RemoteSubtaskDeleteResult
import com.example.rachapro.data.repository.RemoteSubtaskOperationResult
import com.example.rachapro.data.repository.RemoteSubtasksResult
import com.example.rachapro.network.dto.SubtaskResponse

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


    fun loadSubtasks(
        activityId: Long
    ) {

        if (activityId <= 0) {

            _uiState.value =
                SubtasksUiState.Error(
                    message = "La actividad no es válida."
                )

            return
        }

        currentActivityId = activityId

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
                                .fetchRemoteSubtasks(
                                    activityId = activityId
                                )
                    ) {

                        is RemoteSubtasksResult.Success -> {

                            val subtasks =
                                result.subtasks.map { subtask ->
                                    subtask.toEntity()
                                }

                            _uiState.value =
                                SubtasksUiState.Success(
                                    userId = userId,
                                    activityId = activityId,
                                    subtasks = subtasks
                                )
                        }

                        RemoteSubtasksResult.NotFound -> {

                            _uiState.value =
                                SubtasksUiState.Error(
                                    message =
                                        "La actividad no existe o no está disponible."
                                )
                        }

                        RemoteSubtasksResult.Unauthorized -> {

                            _uiState.value =
                                SubtasksUiState.NoActiveSession
                        }

                        RemoteSubtasksResult.Error -> {

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

    fun createSubtask(
        title: String
    ) {

        val currentState =
            _uiState.value

        if (currentState !is SubtasksUiState.Success) {

            _actionState.value =
                SubtaskActionState.Error(
                    message =
                        "No fue posible obtener la actividad."
                )

            return
        }

        if (_actionState.value is SubtaskActionState.Creating) {
            return
        }

        viewModelScope.launch {

            _actionState.value =
                SubtaskActionState.Creating

            when (
                val result =
                    subtaskRepository
                        .createRemoteSubtask(
                            activityId =
                                currentState.activityId,
                            title = title
                        )
            ) {

                is RemoteSubtaskOperationResult.Success -> {

                    _actionState.value =
                        SubtaskActionState.CreateSuccess(
                            subtaskId = result.subtask.id
                        )

                    loadSubtasks(
                        activityId =
                            currentState.activityId
                    )
                }

                RemoteSubtaskOperationResult.InvalidData -> {

                    _actionState.value =
                        SubtaskActionState.ValidationError(
                            message =
                                "Escribe el nombre de la subtarea."
                        )
                }

                RemoteSubtaskOperationResult.NotFound -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "La actividad ya no está disponible."
                        )
                }

                RemoteSubtaskOperationResult.Unauthorized -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "La sesión expiró. Inicia sesión nuevamente."
                        )
                }

                RemoteSubtaskOperationResult.Error -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "No fue posible crear la subtarea."
                        )
                }
            }
        }
    }

    fun updateSubtask(
        subtaskId: Long,
        title: String
    ) {

        val currentState =
            _uiState.value

        if (currentState !is SubtasksUiState.Success) {
            return
        }

        viewModelScope.launch {

            _actionState.value =
                SubtaskActionState.Updating(
                    subtaskId = subtaskId
                )

            when (
                subtaskRepository
                    .updateRemoteSubtask(
                        activityId =
                            currentState.activityId,
                        subtaskId = subtaskId,
                        title = title
                    )
            ) {

                is RemoteSubtaskOperationResult.Success -> {

                    _actionState.value =
                        SubtaskActionState.UpdateSuccess(
                            subtaskId = subtaskId
                        )

                    loadSubtasks(
                        activityId =
                            currentState.activityId
                    )
                }

                RemoteSubtaskOperationResult.InvalidData -> {

                    _actionState.value =
                        SubtaskActionState.ValidationError(
                            message =
                                "El nombre de la subtarea no puede estar vacío."
                        )
                }

                RemoteSubtaskOperationResult.NotFound -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "La subtarea ya no está disponible."
                        )
                }

                RemoteSubtaskOperationResult.Unauthorized -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "La sesión expiró. Inicia sesión nuevamente."
                        )
                }

                RemoteSubtaskOperationResult.Error -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "No fue posible editar la subtarea."
                        )
                }
            }
        }
    }

    fun setSubtaskCompleted(
        subtaskId: Long,
        isCompleted: Boolean
    ) {

        val currentState =
            _uiState.value

        if (currentState !is SubtasksUiState.Success) {
            return
        }

        viewModelScope.launch {

            _actionState.value =
                SubtaskActionState.ChangingCompletion(
                    subtaskId = subtaskId
                )

            when (
                subtaskRepository
                    .setRemoteSubtaskCompleted(
                        activityId =
                            currentState.activityId,
                        subtaskId = subtaskId,
                        isCompleted = isCompleted
                    )
            ) {

                is RemoteSubtaskOperationResult.Success -> {

                    _actionState.value =
                        SubtaskActionState.CompletionSuccess(
                            subtaskId = subtaskId
                        )

                    loadSubtasks(
                        activityId =
                            currentState.activityId
                    )
                }

                RemoteSubtaskOperationResult.NotFound -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "La subtarea ya no está disponible."
                        )
                }

                RemoteSubtaskOperationResult.InvalidData -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "No fue posible cambiar el estado."
                        )
                }

                RemoteSubtaskOperationResult.Unauthorized -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "La sesión expiró. Inicia sesión nuevamente."
                        )
                }

                RemoteSubtaskOperationResult.Error -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "No fue posible cambiar el estado de la subtarea."
                        )
                }
            }
        }
    }

    fun deleteSubtask(
        subtaskId: Long
    ) {

        val currentState =
            _uiState.value

        if (currentState !is SubtasksUiState.Success) {
            return
        }

        viewModelScope.launch {

            _actionState.value =
                SubtaskActionState.Deleting(
                    subtaskId = subtaskId
                )

            when (
                subtaskRepository
                    .deleteRemoteSubtask(
                        activityId =
                            currentState.activityId,
                        subtaskId = subtaskId
                    )
            ) {

                RemoteSubtaskDeleteResult.Success -> {

                    _actionState.value =
                        SubtaskActionState.DeleteSuccess(
                            subtaskId = subtaskId
                        )

                    loadSubtasks(
                        activityId =
                            currentState.activityId
                    )
                }

                RemoteSubtaskDeleteResult.NotFound -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "La subtarea ya no está disponible."
                        )
                }

                RemoteSubtaskDeleteResult.Unauthorized -> {

                    _actionState.value =
                        SubtaskActionState.Error(
                            message =
                                "La sesión expiró. Inicia sesión nuevamente."
                        )
                }

                RemoteSubtaskDeleteResult.Error -> {

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

    private fun SubtaskResponse.toEntity(): SubtaskEntity {

        return SubtaskEntity(
            id = id,
            activityId = activityId,
            title = title,
            isCompleted = isCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt,
            completedAt = completedAt
        )
    }
}

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