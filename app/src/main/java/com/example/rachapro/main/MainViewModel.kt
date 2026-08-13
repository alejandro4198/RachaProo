package com.example.rachapro.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.rachapro.RachaProApplication
import com.example.rachapro.data.local.SessionManager
import com.example.rachapro.data.local.entity.ActivityEntity
import com.example.rachapro.data.local.entity.ActivityStatus
import com.example.rachapro.data.repository.ActivityRepository
import com.example.rachapro.data.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.example.rachapro.data.repository.PomodoroRepository
import com.example.rachapro.domain.StreakCalculator


class MainViewModel(
    private val userRepository: UserRepository,
    private val activityRepository: ActivityRepository,
    private val pomodoroRepository: PomodoroRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MainUiState>(
            MainUiState.Loading
        )

    val uiState: StateFlow<MainUiState> =
        _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadDashboard()
    }

    fun retry() {
        loadDashboard()
    }

    private fun loadDashboard() {

        loadJob?.cancel()

        loadJob =
            viewModelScope.launch {

                _uiState.value =
                    MainUiState.Loading

                try {

                    val session =
                        sessionManager
                            .sessionState
                            .first()

                    val userId =
                        session.userId

                    if (userId == null) {

                        _uiState.value =
                            MainUiState.NoActiveSession

                        return@launch
                    }

                    val user =
                        userRepository.getUserById(
                            userId = userId
                        )

                    if (user == null) {

                        _uiState.value =
                            MainUiState.UserNotFound

                        return@launch
                    }

                    val todayEpochDay =
                        LocalDate
                            .now()
                            .toEpochDay()


                    combine(
                        activityRepository
                            .observeActivitiesByDate(
                                userId = userId,
                                epochDay = todayEpochDay
                            ),

                        activityRepository
                            .observeCompletedDays(
                                userId = userId
                            ),

                        pomodoroRepository
                            .observeCompletedFocusDays(
                                userId = userId
                            ),

                        pomodoroRepository
                            .observeCompletedFocusCountBetween(
                                userId = userId,
                                startEpochDay = todayEpochDay,
                                endEpochDay = todayEpochDay
                            ),

                        pomodoroRepository
                            .observeCompletedFocusSecondsBetween(
                                userId = userId,
                                startEpochDay = todayEpochDay,
                                endEpochDay = todayEpochDay
                            )

                    ) {
                            activities,
                            activityDays,
                            pomodoroDays,
                            todayPomodoros,
                            todayFocusSeconds ->

                        val totalActivitiesToday =
                            activities.size

                        val completedActivitiesToday =
                            activities.count { activity ->
                                activity.status ==
                                        ActivityStatus.COMPLETED
                            }

                        val validStreakDays =
                            (activityDays + pomodoroDays)
                                .distinct()
                                .sorted()

                        val streakResult =
                            StreakCalculator.calculate(
                                completedDays = validStreakDays,
                                todayEpochDay = todayEpochDay
                            )

                        MainUiState.Success(
                            userId = user.id,
                            fullName = user.fullName,
                            email = user.email,
                            semester = user.semester,

                            totalActivitiesToday =
                                totalActivitiesToday,

                            completedActivitiesToday =
                                completedActivitiesToday,

                            todayActivities =
                                activities,

                            currentStreakDays =
                                streakResult.current,

                            bestStreakDays =
                                streakResult.best,

                            todayCompletedPomodoros =
                                todayPomodoros,

                            todayFocusSeconds =
                                todayFocusSeconds
                        )

                    }.collect { state ->

                        _uiState.value =
                            state
                    }

                } catch (_: Exception) {

                    _uiState.value =
                        MainUiState.Error
                }
            }
    }

    companion object {

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {

                initializer {

                    val application =
                        this[APPLICATION_KEY]
                                as RachaProApplication

                    MainViewModel(
                        userRepository = application.userRepository,
                        activityRepository = application.activityRepository,
                        sessionManager = application.sessionManager,
                        pomodoroRepository = application.pomodoroRepository,
                    )
                }
            }
    }
}

sealed interface MainUiState {

    data object Loading :
        MainUiState

    data class Success(
        val userId: Long,
        val fullName: String,
        val email: String,
        val semester: Int,

        val totalActivitiesToday: Int,

        val completedActivitiesToday: Int,

        val todayActivities: List<ActivityEntity>,

        val currentStreakDays: Int,

        val bestStreakDays: Int,

        val todayCompletedPomodoros: Int,

        val todayFocusSeconds: Long
    ) : MainUiState

    data object NoActiveSession :
        MainUiState

    data object UserNotFound :
        MainUiState

    data object Error :
        MainUiState
}