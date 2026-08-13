package com.example.rachapro.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.rachapro.RachaProApplication
import com.example.rachapro.data.local.SessionManager
import com.example.rachapro.data.repository.ActivityRepository
import com.example.rachapro.data.repository.PomodoroRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.example.rachapro.domain.StreakCalculator

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModel(
    private val activityRepository: ActivityRepository,
    private val pomodoroRepository: PomodoroRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ProgressUiState>(
            ProgressUiState.Loading
        )

    val uiState: StateFlow<ProgressUiState> =
        _uiState.asStateFlow()

    private val _selectedPeriod =
        MutableStateFlow(
            ProgressPeriod.ALL
        )

    private var loadJob: Job? = null

    init {
        loadProgress()
    }

    fun retry() {
        loadProgress()
    }

    fun selectPeriod(
        period: ProgressPeriod
    ) {
        _selectedPeriod.value = period
    }

    private fun loadProgress() {

        loadJob?.cancel()

        loadJob =
            viewModelScope.launch {

                _uiState.value =
                    ProgressUiState.Loading

                try {

                    val session =
                        sessionManager
                            .sessionState
                            .first()

                    val userId =
                        session.userId

                    if (userId == null) {

                        _uiState.value =
                            ProgressUiState.NoActiveSession

                        return@launch
                    }

                    val streakDaysFlow =
                        combine(
                            activityRepository
                                .observeCompletedDays(
                                    userId = userId
                                ),

                            pomodoroRepository
                                .observeCompletedFocusDays(
                                    userId = userId
                                )
                        ) { activityDays, pomodoroDays ->

                            (activityDays + pomodoroDays)
                                .distinct()
                                .sorted()
                        }

                    val periodStatsFlow =
                        _selectedPeriod
                            .flatMapLatest { period ->

                                val today =
                                    LocalDate.now()

                                when (period) {

                                    ProgressPeriod.TODAY -> {

                                        val epochDay =
                                            today.toEpochDay()

                                        combine(
                                            activityRepository
                                                .observeCompletedActivitiesCountBetween(
                                                    userId = userId,
                                                    startEpochDay = epochDay,
                                                    endEpochDay = epochDay
                                                ),

                                            pomodoroRepository
                                                .observeCompletedFocusCountBetween(
                                                    userId = userId,
                                                    startEpochDay = epochDay,
                                                    endEpochDay = epochDay
                                                ),

                                            pomodoroRepository
                                                .observeCompletedFocusSecondsBetween(
                                                    userId = userId,
                                                    startEpochDay = epochDay,
                                                    endEpochDay = epochDay
                                                )
                                        ) {
                                                activities,
                                                pomodoros,
                                                focusSeconds ->

                                            PeriodStats(
                                                period = period,
                                                completedActivities =
                                                    activities,
                                                completedPomodoros =
                                                    pomodoros,
                                                focusSeconds =
                                                    focusSeconds
                                            )
                                        }
                                    }

                                    ProgressPeriod.WEEK -> {

                                        val startDate =
                                            today.minusDays(
                                                (
                                                        today.dayOfWeek.value - 1
                                                        ).toLong()
                                            )

                                        val startEpochDay =
                                            startDate.toEpochDay()

                                        val endEpochDay =
                                            startDate
                                                .plusDays(6)
                                                .toEpochDay()

                                        combine(
                                            activityRepository
                                                .observeCompletedActivitiesCountBetween(
                                                    userId = userId,
                                                    startEpochDay =
                                                        startEpochDay,
                                                    endEpochDay =
                                                        endEpochDay
                                                ),

                                            pomodoroRepository
                                                .observeCompletedFocusCountBetween(
                                                    userId = userId,
                                                    startEpochDay =
                                                        startEpochDay,
                                                    endEpochDay =
                                                        endEpochDay
                                                ),

                                            pomodoroRepository
                                                .observeCompletedFocusSecondsBetween(
                                                    userId = userId,
                                                    startEpochDay =
                                                        startEpochDay,
                                                    endEpochDay =
                                                        endEpochDay
                                                )
                                        ) {
                                                activities,
                                                pomodoros,
                                                focusSeconds ->

                                            PeriodStats(
                                                period = period,
                                                completedActivities =
                                                    activities,
                                                completedPomodoros =
                                                    pomodoros,
                                                focusSeconds =
                                                    focusSeconds
                                            )
                                        }
                                    }

                                    ProgressPeriod.ALL -> {

                                        combine(
                                            activityRepository
                                                .observeCompletedActivitiesCount(
                                                    userId = userId
                                                ),

                                            pomodoroRepository
                                                .observeCompletedFocusCount(
                                                    userId = userId
                                                ),

                                            pomodoroRepository
                                                .observeCompletedFocusSeconds(
                                                    userId = userId
                                                )
                                        ) {
                                                activities,
                                                pomodoros,
                                                focusSeconds ->

                                            PeriodStats(
                                                period = period,
                                                completedActivities =
                                                    activities,
                                                completedPomodoros =
                                                    pomodoros,
                                                focusSeconds =
                                                    focusSeconds
                                            )
                                        }
                                    }
                                }
                            }

                    val today =
                        LocalDate.now()

                    val startOfWeek =
                        today.minusDays(
                            (
                                    today.dayOfWeek.value - 1
                                    ).toLong()
                        )

                    val startWeekEpochDay =
                        startOfWeek.toEpochDay()

                    val endWeekEpochDay =
                        startOfWeek
                            .plusDays(6)
                            .toEpochDay()

                    val weeklyProgressFlow =
                        combine(
                            activityRepository
                                .observeCompletedActivitiesByDay(
                                    userId = userId,
                                    startEpochDay =
                                        startWeekEpochDay,
                                    endEpochDay =
                                        endWeekEpochDay
                                ),

                            pomodoroRepository
                                .observeCompletedFocusStatsByDay(
                                    userId = userId,
                                    startEpochDay =
                                        startWeekEpochDay,
                                    endEpochDay =
                                        endWeekEpochDay
                                )
                        ) { activityStats, pomodoroStats ->

                            val activitiesByDay =
                                activityStats.associateBy {
                                    it.epochDay
                                }

                            val pomodorosByDay =
                                pomodoroStats.associateBy {
                                    it.epochDay
                                }

                            (0L..6L).map { offset ->

                                val epochDay =
                                    startWeekEpochDay + offset

                                val activityDay =
                                    activitiesByDay[epochDay]

                                val pomodoroDay =
                                    pomodorosByDay[epochDay]

                                WeeklyProgressDay(
                                    epochDay = epochDay,
                                    completedActivities =
                                        activityDay?.count ?: 0,
                                    completedPomodoros =
                                        pomodoroDay?.pomodoroCount ?: 0,
                                    focusSeconds =
                                        pomodoroDay?.focusSeconds ?: 0L
                                )
                            }
                        }

                    combine(
                        periodStatsFlow,
                        streakDaysFlow,
                        weeklyProgressFlow
                    ) {
                            stats,
                            validDays,
                            weeklyDays ->

                        val todayEpochDay =
                            LocalDate
                                .now()
                                .toEpochDay()

                        val streaks =
                            StreakCalculator.calculate(
                                completedDays = validDays,
                                todayEpochDay = todayEpochDay
                            )

                        ProgressUiState.Success(
                            completedActivities =
                                stats.completedActivities,

                            completedPomodoros =
                                stats.completedPomodoros,

                            totalFocusSeconds =
                                stats.focusSeconds,

                            currentStreakDays =
                                streaks.current,

                            bestStreakDays =
                                streaks.best,

                            selectedPeriod =
                                stats.period,

                            weeklyDays =
                                weeklyDays
                        )

                    }.collect { state ->

                        _uiState.value =
                            state
                    }

                } catch (_: Exception) {

                    _uiState.value =
                        ProgressUiState.Error
                }
            }
    }

    companion object {

        val Factory:
                ViewModelProvider.Factory =
            viewModelFactory {

                initializer {

                    val application =
                        this[APPLICATION_KEY]
                                as RachaProApplication

                    ProgressViewModel(
                        activityRepository =
                            application.activityRepository,

                        pomodoroRepository =
                            application.pomodoroRepository,

                        sessionManager =
                            application.sessionManager
                    )
                }
            }
    }
}

sealed interface ProgressUiState {

    data object Loading :
        ProgressUiState

    data class Success(
        val completedActivities: Int,
        val completedPomodoros: Int,
        val totalFocusSeconds: Long,
        val currentStreakDays: Int,
        val bestStreakDays: Int,
        val selectedPeriod: ProgressPeriod,
        val weeklyDays: List<WeeklyProgressDay>
    ) : ProgressUiState

    data object NoActiveSession :
        ProgressUiState

    data object Error :
        ProgressUiState
}

enum class ProgressPeriod {
    TODAY,
    WEEK,
    ALL
}

data class WeeklyProgressDay(
    val epochDay: Long,
    val completedActivities: Int,
    val completedPomodoros: Int,
    val focusSeconds: Long
)

private data class PeriodStats(
    val period: ProgressPeriod,
    val completedActivities: Int,
    val completedPomodoros: Int,
    val focusSeconds: Long
)
