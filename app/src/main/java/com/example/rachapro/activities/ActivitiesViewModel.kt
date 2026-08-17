package com.example.rachapro.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.rachapro.RachaProApplication
import com.example.rachapro.data.local.SessionManager
import com.example.rachapro.data.local.entity.ActivityEntity
import com.example.rachapro.data.local.entity.CategoryEntity
import com.example.rachapro.data.repository.ActivityCreateResult
import com.example.rachapro.data.repository.ActivityOperationResult
import com.example.rachapro.data.repository.ActivityRepository
import com.example.rachapro.data.local.entity.ActivityStatus
import com.example.rachapro.data.repository.CategoryRepository
import com.example.rachapro.data.local.entity.ActivityPriority
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class ActivitiesViewModel(
    private val activityRepository: ActivityRepository,
    private val categoryRepository: CategoryRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ActivitiesUiState>(
            ActivitiesUiState.Loading
        )

    val uiState: StateFlow<ActivitiesUiState> =
        _uiState.asStateFlow()

    private val _actionState =
        MutableStateFlow<ActivityActionState>(
            ActivityActionState.Idle
        )

    val actionState: StateFlow<ActivityActionState> =
        _actionState.asStateFlow()

    private val _selectedFilter =
        MutableStateFlow(
            ActivityFilter.ALL
        )

    val selectedFilter: StateFlow<ActivityFilter> =
        _selectedFilter.asStateFlow()

    private val _searchQuery =
        MutableStateFlow("")

    val searchQuery: StateFlow<String> =
        _searchQuery.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadData()
    }

    fun retry() {
        loadData()
    }

    fun selectFilter(
        filter: ActivityFilter
    ) {

        _selectedFilter.value =
            filter
    }

    fun updateSearchQuery(
        query: String
    ) {

        _searchQuery.value =
            query
    }

    fun refreshStatuses() {

        val currentState =
            _uiState.value

        if (
            currentState
                    !is ActivitiesUiState.Success
        ) {
            return
        }

        viewModelScope.launch {

            try {

                refreshActivityStatuses(
                    userId =
                        currentState.userId
                )

            } catch (_: Exception) {

                _actionState.value =
                    ActivityActionState.Error(
                        message =
                            "No fue posible actualizar los estados."
                    )
            }
        }
    }


    fun createActivity(
        title: String,
        description: String,
        categoryId: Long?,
        dueDateEpochDay: Long?,
        dueTimeMinutes: Int?,
        priority: String
    ) {

        if (_actionState.value is ActivityActionState.Saving) {
            return
        }

        val currentState =
            _uiState.value

        if (currentState !is ActivitiesUiState.Success) {

            _actionState.value =
                ActivityActionState.Error(
                    message =
                        "No fue posible obtener el usuario activo."
                )

            return
        }

        if (categoryId == null) {

            _actionState.value =
                ActivityActionState.ValidationError(
                    message =
                        "Selecciona una categoría."
                )

            return
        }

        if (dueDateEpochDay == null) {

            _actionState.value =
                ActivityActionState.ValidationError(
                    message =
                        "Selecciona una fecha límite."
                )

            return
        }

        viewModelScope.launch {

            _actionState.value =
                ActivityActionState.Saving

            when (
                val result =
                    activityRepository.createActivity(
                        userId =
                            currentState.userId,

                        categoryId =
                            categoryId,

                        title =
                            title,

                        description =
                            description,

                        dueDateEpochDay =
                            dueDateEpochDay,

                        dueTimeMinutes =
                            dueTimeMinutes,

                        priority =
                            priority,

                        repeatRule =
                            null
                    )
            ) {

                is ActivityCreateResult.Success -> {

                    refreshActivityStatuses(
                        userId =
                            currentState.userId
                    )

                    _actionState.value =
                        ActivityActionState.CreateSuccess(
                            activityId =
                                result.activityId
                        )
                }

                is ActivityCreateResult.InvalidData -> {

                    _actionState.value =
                        ActivityActionState.ValidationError(
                            message =
                                result.message
                        )
                }

                ActivityCreateResult.CategoryNotFound -> {

                    _actionState.value =
                        ActivityActionState.ValidationError(
                            message =
                                "La categoría seleccionada ya no existe."
                        )
                }

                ActivityCreateResult.Error -> {

                    _actionState.value =
                        ActivityActionState.Error(
                            message =
                                "No fue posible crear la actividad."
                        )
                }
            }
        }
    }

    fun updateActivity(
        activityId: Long,
        title: String,
        description: String,
        categoryId: Long?,
        dueDateEpochDay: Long?,
        dueTimeMinutes: Int?,
        priority: String
    ) {

        if (
            _actionState.value
                    is ActivityActionState.Updating
        ) {
            return
        }

        val currentState =
            _uiState.value

        if (
            currentState
                    !is ActivitiesUiState.Success
        ) {

            _actionState.value =
                ActivityActionState.Error(
                    message =
                        "No fue posible obtener el usuario activo."
                )

            return
        }

        if (categoryId == null) {

            _actionState.value =
                ActivityActionState.ValidationError(
                    message =
                        "Selecciona una categoría."
                )

            return
        }

        if (dueDateEpochDay == null) {

            _actionState.value =
                ActivityActionState.ValidationError(
                    message =
                        "Selecciona una fecha límite."
                )

            return
        }

        viewModelScope.launch {

            _actionState.value =
                ActivityActionState.Updating(
                    activityId = activityId
                )

            when (
                activityRepository.updateActivity(
                    activityId = activityId,
                    userId = currentState.userId,
                    categoryId = categoryId,
                    title = title,
                    description = description,
                    dueDateEpochDay =
                        dueDateEpochDay,
                    dueTimeMinutes =
                        dueTimeMinutes,
                    priority = priority,
                    repeatRule = null
                )
            ) {

                ActivityOperationResult.Success -> {

                    refreshActivityStatuses(
                        userId =
                            currentState.userId
                    )

                    _actionState.value =
                        ActivityActionState.UpdateSuccess(
                            activityId =
                                activityId
                        )
                }

                ActivityOperationResult.NotFoundOrNotAllowed -> {

                    _actionState.value =
                        ActivityActionState.Error(
                            message =
                                "La actividad no existe o ya no puede editarse."
                        )
                }

                ActivityOperationResult.InvalidData -> {

                    _actionState.value =
                        ActivityActionState.ValidationError(
                            message =
                                "Revisa los datos ingresados."
                        )
                }

                ActivityOperationResult.Error -> {

                    _actionState.value =
                        ActivityActionState.Error(
                            message =
                                "Ocurrió un error al editar la actividad."
                        )
                }
            }
        }
    }

    fun completeActivity(
        activityId: Long
    ) {

        if (
            _actionState.value
                    is ActivityActionState.Completing
        ) {
            return
        }

        val currentState =
            _uiState.value

        if (
            currentState
                    !is ActivitiesUiState.Success
        ) {

            _actionState.value =
                ActivityActionState.Error(
                    message =
                        "No fue posible obtener el usuario activo."
                )

            return
        }

        viewModelScope.launch {

            _actionState.value =
                ActivityActionState.Completing(
                    activityId = activityId
                )

            val completedDateEpochDay =
                LocalDate
                    .now()
                    .toEpochDay()

            when (
                activityRepository.completeActivity(
                    activityId = activityId,
                    userId = currentState.userId,
                    completedDateEpochDay =
                        completedDateEpochDay
                )
            ) {

                ActivityOperationResult.Success -> {

                    _actionState.value =
                        ActivityActionState.CompleteSuccess(
                            activityId = activityId
                        )
                }

                ActivityOperationResult.NotFoundOrNotAllowed -> {

                    _actionState.value =
                        ActivityActionState.Error(
                            message =
                                "La actividad no existe o ya fue completada."
                        )
                }

                ActivityOperationResult.InvalidData -> {

                    _actionState.value =
                        ActivityActionState.Error(
                            message =
                                "No fue posible completar la actividad."
                        )
                }

                ActivityOperationResult.Error -> {

                    _actionState.value =
                        ActivityActionState.Error(
                            message =
                                "Ocurrió un error al completar la actividad."
                        )
                }
            }
        }
    }

    fun deleteActivity(
        activityId: Long
    ) {

        if (
            _actionState.value
                    is ActivityActionState.Deleting
        ) {
            return
        }

        val currentState =
            _uiState.value

        if (
            currentState
                    !is ActivitiesUiState.Success
        ) {

            _actionState.value =
                ActivityActionState.Error(
                    message =
                        "No fue posible obtener el usuario activo."
                )

            return
        }

        viewModelScope.launch {

            _actionState.value =
                ActivityActionState.Deleting(
                    activityId = activityId
                )

            when (
                activityRepository.softDeleteActivity(
                    activityId = activityId,
                    userId = currentState.userId
                )
            ) {

                ActivityOperationResult.Success -> {

                    _actionState.value =
                        ActivityActionState.DeleteSuccess(
                            activityId = activityId
                        )
                }

                ActivityOperationResult.NotFoundOrNotAllowed -> {

                    _actionState.value =
                        ActivityActionState.Error(
                            message =
                                "La actividad no existe o ya fue eliminada."
                        )
                }

                ActivityOperationResult.InvalidData -> {

                    _actionState.value =
                        ActivityActionState.Error(
                            message =
                                "No fue posible eliminar la actividad."
                        )
                }

                ActivityOperationResult.Error -> {

                    _actionState.value =
                        ActivityActionState.Error(
                            message =
                                "Ocurrió un error al eliminar la actividad."
                        )
                }
            }
        }
    }

    fun resetActionState() {

        _actionState.value =
            ActivityActionState.Idle
    }


    private suspend fun refreshActivityStatuses(
        userId: Long
    ) {

        val today =
            LocalDate.now()

        val currentTime =
            LocalTime.now()

        val currentTimeMinutes =
            (currentTime.hour * 60) +
                    currentTime.minute

        activityRepository
            .refreshActivityStatuses(
                userId = userId,
                todayEpochDay =
                    today.toEpochDay(),
                currentTimeMinutes =
                    currentTimeMinutes
            )
    }

    private fun loadData() {

        loadJob?.cancel()

        loadJob =
            viewModelScope.launch {

                _uiState.value =
                    ActivitiesUiState.Loading

                try {

                    val session =
                        sessionManager
                            .sessionState
                            .first()

                    val userId =
                        session.userId

                    if (userId == null) {

                        _uiState.value =
                            ActivitiesUiState.NoActiveSession

                        return@launch
                    }

                    ensureDefaultCategories(
                        userId = userId
                    )

                    refreshActivityStatuses(
                        userId = userId
                    )

                    combine(
                        categoryRepository.observeCategories(
                            userId = userId
                        ),

                        activityRepository.observeActivities(
                            userId = userId
                        ),

                        _selectedFilter,

                        _searchQuery

                    ) { categories, activities, selectedFilter, searchQuery ->

                        val filteredByStatus = filterActivities(
                                activities = activities,
                                filter = selectedFilter
                            )

                        val searchedActivities =
                            searchActivities(
                                activities =
                                    filteredByStatus,
                                query =
                                    searchQuery
                            )

                        val filteredActivities =
                            sortActivities(
                                activities =
                                    searchedActivities
                            )

                        ActivitiesUiState.Success(
                            userId = userId,
                            categories = categories,
                            activities = activities,
                            filteredActivities = filteredActivities,
                            selectedFilter = selectedFilter,
                            searchQuery = searchQuery,
                        )

                    }.collect { state ->

                        _uiState.value =
                            state
                    }

                } catch (_: Exception) {

                    _uiState.value =
                        ActivitiesUiState.Error
                }
            }
    }


    private fun filterActivities(
        activities: List<ActivityEntity>,
        filter: ActivityFilter
    ): List<ActivityEntity> {

        val todayEpochDay =
            LocalDate
                .now()
                .toEpochDay()

        return when (filter) {

            ActivityFilter.ALL -> {

                activities
            }

            ActivityFilter.TODAY -> {

                activities.filter { activity ->

                    activity.dueDateEpochDay ==
                            todayEpochDay
                }
            }

            ActivityFilter.PENDING -> {

                activities.filter { activity ->

                    activity.status ==
                            ActivityStatus.PENDING
                }
            }

            ActivityFilter.OVERDUE -> {

                activities.filter { activity ->

                    activity.status ==
                            ActivityStatus.OVERDUE
                }
            }

            ActivityFilter.COMPLETED -> {

                activities.filter { activity ->

                    activity.status ==
                            ActivityStatus.COMPLETED
                }
            }
        }
    }

    private fun searchActivities(
        activities: List<ActivityEntity>,
        query: String
    ): List<ActivityEntity> {

        val normalizedQuery =
            query.trim()

        if (normalizedQuery.isBlank()) {
            return activities
        }

        return activities.filter { activity ->

            activity.title.contains(
                normalizedQuery,
                ignoreCase = true
            ) ||
                    activity.description.contains(
                        normalizedQuery,
                        ignoreCase = true
                    )
        }
    }

    private fun sortActivities(
        activities: List<ActivityEntity>
    ): List<ActivityEntity> {

        val todayEpochDay =
            LocalDate
                .now()
                .toEpochDay()

        return activities.sortedWith(

            compareBy<ActivityEntity> { activity ->

                when {

                    activity.status ==
                            ActivityStatus.OVERDUE -> {

                        0
                    }

                    activity.status ==
                            ActivityStatus.PENDING &&
                            activity.dueDateEpochDay ==
                            todayEpochDay -> {

                        1
                    }

                    activity.status ==
                            ActivityStatus.PENDING -> {

                        2
                    }

                    activity.status ==
                            ActivityStatus.COMPLETED -> {

                        3
                    }

                    else -> {

                        4
                    }
                }
            }

                /*
                 * Primero la fecha más cercana.
                 */
                .thenBy { activity ->

                    activity.dueDateEpochDay
                }

                .thenBy { activity ->

                    activity.dueTimeMinutes
                        ?: Int.MAX_VALUE
                }

                .thenBy { activity ->

                    priorityOrder(
                        priority =
                            activity.priority
                    )
                }
        )
    }

    private fun priorityOrder(
        priority: String
    ): Int {

        return when (priority) {

            ActivityPriority.HIGH ->
                0

            ActivityPriority.MEDIUM ->
                1

            ActivityPriority.LOW ->
                2

            else ->
                3
        }
    }

    private suspend fun ensureDefaultCategories(
        userId: Long
    ) {

        val currentCategories =
            categoryRepository
                .observeCategories(
                    userId = userId
                )
                .first()

        if (currentCategories.isNotEmpty()) {
            return
        }

        categoryRepository.createCategory(
            userId = userId,
            name = "Estudio",
            icon = "📚"
        )

        categoryRepository.createCategory(
            userId = userId,
            name = "Personal",
            icon = "👤"
        )

        categoryRepository.createCategory(
            userId = userId,
            name = "Trabajo",
            icon = "💼"
        )
    }

    companion object {

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {

                initializer {

                    val application =
                        this[APPLICATION_KEY]
                                as RachaProApplication

                    ActivitiesViewModel(
                        activityRepository =
                            application.activityRepository,

                        categoryRepository =
                            application.categoryRepository,

                        sessionManager =
                            application.sessionManager
                    )
                }
            }
    }
}


sealed interface ActivitiesUiState {
    data object Loading :
        ActivitiesUiState

    data class Success(
        val userId: Long,
        val categories: List<CategoryEntity>,
        val activities: List<ActivityEntity>,
        val filteredActivities: List<ActivityEntity>,
        val selectedFilter: ActivityFilter,
        val searchQuery: String
    ) : ActivitiesUiState

    data object NoActiveSession :
        ActivitiesUiState

    data object Error :
        ActivitiesUiState
}

enum class ActivityFilter {

    ALL,

    TODAY,

    PENDING,

    OVERDUE,

    COMPLETED
}


sealed interface ActivityActionState {

    data object Idle :
        ActivityActionState

    data object Saving :
        ActivityActionState

    data class CreateSuccess(
        val activityId: Long
    ) : ActivityActionState

    data class Updating(
        val activityId: Long
    ) : ActivityActionState

    data class UpdateSuccess(
        val activityId: Long
    ) : ActivityActionState

    data class ValidationError(
        val message: String
    ) : ActivityActionState

    data class Error(
        val message: String
    ) : ActivityActionState

    data class Completing(
        val activityId: Long
    ) : ActivityActionState

    data class CompleteSuccess(
        val activityId: Long
    ) : ActivityActionState

    data class Deleting(
        val activityId: Long
    ) : ActivityActionState

    data class DeleteSuccess(
        val activityId: Long
    ) : ActivityActionState
}