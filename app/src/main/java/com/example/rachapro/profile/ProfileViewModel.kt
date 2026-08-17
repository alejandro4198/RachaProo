package com.example.rachapro.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.rachapro.RachaProApplication
import com.example.rachapro.data.local.PomodoroPreferences
import com.example.rachapro.data.local.SessionManager
import com.example.rachapro.data.local.UserPreferences
import com.example.rachapro.data.local.UserPreferencesManager
import com.example.rachapro.data.local.entity.AchievementEntity
import com.example.rachapro.data.repository.AchievementRepository
import com.example.rachapro.data.repository.ActivityRepository
import com.example.rachapro.data.repository.PomodoroRepository
import com.example.rachapro.data.repository.UpdateProfileResult
import com.example.rachapro.data.repository.UserRepository
import com.example.rachapro.domain.AchievementCheckInput
import com.example.rachapro.domain.AchievementEngine
import com.example.rachapro.domain.StreakCalculator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val userPreferencesManager: UserPreferencesManager,
    private val achievementRepository: AchievementRepository,
    private val activityRepository: ActivityRepository,
    private val pomodoroRepository: PomodoroRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ProfileUiState>(
            ProfileUiState.Loading
        )

    val uiState: StateFlow<ProfileUiState> =
        _uiState.asStateFlow()

    private val _saveState =
        MutableStateFlow<ProfileSaveState>(
            ProfileSaveState.Idle
        )

    val saveState: StateFlow<ProfileSaveState> =
        _saveState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadProfile()
    }

    fun retry() {
        loadProfile()
    }

    fun updatePomodoroDraft(
        focusMinutes: Int,
        shortBreakMinutes: Int,
        longBreakMinutes: Int
    ) {

        val current =
            _uiState.value

        if (current !is ProfileUiState.Success) {
            return
        }

        _uiState.value =
            current.copy(
                pomodoroDraft = PomodoroPreferences(
                    focusMinutes = focusMinutes,
                    shortBreakMinutes = shortBreakMinutes,
                    longBreakMinutes = longBreakMinutes
                )
            )
    }

    fun updateNotificationsEnabled(
        enabled: Boolean
    ) {

        val current =
            _uiState.value

        if (current !is ProfileUiState.Success) {
            return
        }

        _uiState.value =
            current.copy(
                notificationsEnabled = enabled
            )
    }

    fun updateProfileDraft(
        fullName: String,
        semester: Int
    ) {

        val current =
            _uiState.value

        if (current !is ProfileUiState.Success) {
            return
        }

        _uiState.value =
            current.copy(
                profileDraftName = fullName,
                profileDraftSemester = semester
            )
    }

    fun saveProfile() {

        val current =
            _uiState.value

        if (
            current !is ProfileUiState.Success ||
            _saveState.value is ProfileSaveState.Saving
        ) {
            return
        }

        viewModelScope.launch {

            _saveState.value =
                ProfileSaveState.Saving

            when (
                val result =
                    userRepository.updateProfile(
                        userId = current.userId,
                        fullName = current.profileDraftName,
                        semester = current.profileDraftSemester
                    )
            ) {

                UpdateProfileResult.Success -> {

                    _saveState.value =
                        ProfileSaveState.Success(
                            message = "Perfil actualizado"
                        )

                    _uiState.value =
                        current.copy(
                            fullName = current.profileDraftName.trim(),
                            semester = current.profileDraftSemester
                        )
                }

                is UpdateProfileResult.InvalidData -> {

                    _saveState.value =
                        ProfileSaveState.Error(
                            result.message
                        )
                }

                UpdateProfileResult.NotFound,
                UpdateProfileResult.Error -> {

                    _saveState.value =
                        ProfileSaveState.Error(
                            "No fue posible actualizar el perfil."
                        )
                }
            }
        }
    }

    fun savePreferences() {

        val current =
            _uiState.value

        if (
            current !is ProfileUiState.Success ||
            _saveState.value is ProfileSaveState.Saving
        ) {
            return
        }

        viewModelScope.launch {

            _saveState.value =
                ProfileSaveState.Saving

            try {

                val draft =
                    current.pomodoroDraft

                userPreferencesManager.savePomodoroPreferences(
                    userId = current.userId,
                    focusMinutes = draft.focusMinutes,
                    shortBreakMinutes = draft.shortBreakMinutes,
                    longBreakMinutes = draft.longBreakMinutes
                )

                userPreferencesManager.saveNotificationsEnabled(
                    userId = current.userId,
                    enabled = current.notificationsEnabled
                )

                _saveState.value =
                    ProfileSaveState.Success(
                        message = "Preferencias guardadas"
                    )

            } catch (_: IllegalArgumentException) {

                _saveState.value =
                    ProfileSaveState.Error(
                        "Los valores ingresados no son válidos."
                    )

            } catch (_: Exception) {

                _saveState.value =
                    ProfileSaveState.Error(
                        "No fue posible guardar las preferencias."
                    )
            }
        }
    }

    fun resetSaveState() {

        _saveState.value =
            ProfileSaveState.Idle
    }

    private fun loadProfile() {

        loadJob?.cancel()

        loadJob =
            viewModelScope.launch {

                _uiState.value =
                    ProfileUiState.Loading

                try {

                    val session =
                        sessionManager
                            .sessionState
                            .first()

                    val userId =
                        session.userId

                    if (userId == null) {

                        _uiState.value =
                            ProfileUiState.NoActiveSession

                        return@launch
                    }

                    val user =
                        userRepository.getUserById(
                            userId = userId
                        )

                    if (user == null) {

                        _uiState.value =
                            ProfileUiState.Error

                        return@launch
                    }

                    combine(
                        userPreferencesManager
                            .observeUserPreferences(
                                userId = userId
                            ),

                        achievementRepository
                            .observeAchievements(
                                userId = userId
                            ),

                        activityRepository
                            .observeCompletedActivitiesCount(
                                userId = userId
                            ),

                        pomodoroRepository
                            .observeCompletedFocusCount(
                                userId = userId
                            ),

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

                            activityDays to pomodoroDays
                        }
                    ) {
                            preferences,
                            achievements,
                            completedActivities,
                            completedPomodoros,
                            daysPair ->

                        val activityDays = daysPair.first
                        val pomodoroDays = daysPair.second

                        val todayEpochDay =
                            LocalDate.now().toEpochDay()

                        val streak =
                            StreakCalculator.calculate(
                                completedDays =
                                    (activityDays + pomodoroDays)
                                        .distinct(),
                                todayEpochDay = todayEpochDay
                            )

                        ProfileData(
                            preferences = preferences,
                            achievements = achievements,
                            completedActivities = completedActivities,
                            completedPomodoros = completedPomodoros,
                            currentStreakDays = streak.current
                        )

                    }.collect { profileData ->

                        achievementRepository.syncAchievements(
                            userId = userId,
                            input = AchievementCheckInput(
                                completedActivitiesCount =
                                    profileData.completedActivities,
                                completedFocusPomodorosCount =
                                    profileData.completedPomodoros,
                                currentStreakDays =
                                    profileData.currentStreakDays
                            )
                        )

                        val state =
                            ProfileUiState.Success(
                                userId = user.id,
                                fullName = user.fullName,
                                email = user.email,
                                semester = user.semester,
                                profileDraftName = user.fullName,
                                profileDraftSemester = user.semester,
                                savedPreferences =
                                    profileData.preferences,
                                pomodoroDraft =
                                    profileData.preferences.pomodoro,
                                notificationsEnabled =
                                    profileData.preferences.notificationsEnabled,
                                achievements =
                                    profileData.achievements,
                                allAchievementTypes =
                                    AchievementEngine.allTypes
                            )

                        val current =
                            _uiState.value

                        if (
                            current is ProfileUiState.Success &&
                            current.userId == state.userId &&
                            _saveState.value !is ProfileSaveState.Saving
                        ) {

                            _uiState.value =
                                state.copy(
                                    pomodoroDraft =
                                        current.pomodoroDraft,
                                    notificationsEnabled =
                                        current.notificationsEnabled,
                                    profileDraftName =
                                        current.profileDraftName,
                                    profileDraftSemester =
                                        current.profileDraftSemester
                                )

                        } else if (
                            _saveState.value !is ProfileSaveState.Saving
                        ) {

                            _uiState.value = state
                        }
                    }

                } catch (_: Exception) {

                    _uiState.value =
                        ProfileUiState.Error
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

                    ProfileViewModel(
                        userRepository =
                            application.userRepository,
                        userPreferencesManager =
                            application.userPreferencesManager,
                        achievementRepository =
                            application.achievementRepository,
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

sealed interface ProfileUiState {

    data object Loading : ProfileUiState

    data class Success(
        val userId: Long,
        val fullName: String,
        val email: String,
        val semester: Int,
        val profileDraftName: String,
        val profileDraftSemester: Int,
        val savedPreferences: UserPreferences,
        val pomodoroDraft: PomodoroPreferences,
        val notificationsEnabled: Boolean,
        val achievements: List<AchievementEntity>,
        val allAchievementTypes: List<String>
    ) : ProfileUiState

    data object NoActiveSession : ProfileUiState

    data object Error : ProfileUiState
}

sealed interface ProfileSaveState {

    data object Idle : ProfileSaveState

    data object Saving : ProfileSaveState

    data class Success(
        val message: String
    ) : ProfileSaveState

    data class Error(
        val message: String
    ) : ProfileSaveState
}

private data class ProfileData(
    val preferences: UserPreferences,
    val achievements: List<AchievementEntity>,
    val completedActivities: Int,
    val completedPomodoros: Int,
    val currentStreakDays: Int
)
