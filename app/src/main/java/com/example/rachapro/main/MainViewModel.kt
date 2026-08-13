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
                            )

                    ) { activities, activityDays, pomodoroDays ->

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
                            calculateStreaks(
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
                                streakResult.best
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

    private fun calculateStreaks(
        completedDays: List<Long>,
        todayEpochDay: Long
    ): StreakResult {


        val validDays =
            completedDays
                .filter { day ->
                    day <= todayEpochDay
                }
                .distinct()
                .sorted()

        if (validDays.isEmpty()) {

            return StreakResult(
                current = 0,
                best = 0
            )
        }

        var bestStreak = 1
        var runningStreak = 1

        for (
        index in 1 until validDays.size
        ) {

            val previousDay =
                validDays[index - 1]

            val currentDay =
                validDays[index]

            if (
                currentDay ==
                previousDay + 1
            ) {

                runningStreak++

            } else {

                runningStreak = 1
            }

            if (
                runningStreak >
                bestStreak
            ) {

                bestStreak =
                    runningStreak
            }
        }

        val validDaysSet =
            validDays.toSet()

        val streakEndDay =
            when {

                todayEpochDay in validDaysSet -> {

                    todayEpochDay
                }

                todayEpochDay - 1
                        in validDaysSet -> {

                    todayEpochDay - 1
                }

                else -> {

                    return StreakResult(
                        current = 0,
                        best = bestStreak
                    )
                }
            }

        var currentStreak = 0

        var dayToCheck =
            streakEndDay

        while (
            dayToCheck in validDaysSet
        ) {

            currentStreak++

            dayToCheck--
        }

        return StreakResult(
            current = currentStreak,
            best = bestStreak
        )
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

private data class StreakResult(
    val current: Int,
    val best: Int
)

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

        val bestStreakDays: Int
    ) : MainUiState

    data object NoActiveSession :
        MainUiState

    data object UserNotFound :
        MainUiState

    data object Error :
        MainUiState
}