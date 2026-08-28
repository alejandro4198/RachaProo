package com.example.rachapro.auth

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rachapro.RachaProApplication
import com.example.rachapro.data.repository.LoginResult
import com.example.rachapro.data.repository.RegisterResult
import com.example.rachapro.data.repository.UserRepository
import com.example.rachapro.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.rachapro.data.repository.ReminderRepository
import com.example.rachapro.notifications.ReminderScheduler
import com.example.rachapro.domain.RegistrationValidator
import kotlinx.coroutines.flow.first


class AuthViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<AuthUiState>(
            AuthUiState.Idle
        )

    val uiState: StateFlow<AuthUiState> =
        _uiState.asStateFlow()

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        semester: Int?,
        acceptedPrivacyPolicy: Boolean
    ) {

        if (_uiState.value is AuthUiState.Loading) {
            return
        }

        val validationError =
            RegistrationValidator.validate(
                fullName = fullName,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                semester = semester,
                acceptedPrivacyPolicy = acceptedPrivacyPolicy
            )

        if (validationError != null) {

            _uiState.value =
                AuthUiState.ValidationError(
                    message = validationError
                )

            return
        }

        viewModelScope.launch {

            _uiState.value =
                AuthUiState.Loading

            when (
                val result =
                    userRepository.registerUser(
                        fullName = fullName,
                        email = email,
                        password = password,
                        semester = semester!!,
                        acceptedPrivacyPolicy =
                            acceptedPrivacyPolicy
                    )
            ) {

                is RegisterResult.Success -> {

                    _uiState.value =
                        AuthUiState.RegistrationSuccess(
                            userId = result.userId
                        )
                }

                RegisterResult.EmailAlreadyRegistered -> {

                    _uiState.value =
                        AuthUiState.EmailAlreadyRegistered
                }

                RegisterResult.Error -> {

                    _uiState.value =
                        AuthUiState.Error
                }
            }
        }
    }

    fun login(
        email: String,
        password: String
    ) {

        if (_uiState.value is AuthUiState.Loading) {
            return
        }

        if (email.isBlank() || password.isBlank()) {

            _uiState.value =
                AuthUiState.ValidationError(
                    message = "Ingresa el correo y la contraseña."
                )

            return
        }

        viewModelScope.launch {

            _uiState.value =
                AuthUiState.Loading

            when (
                val result =
                    userRepository.login(
                        email = email,
                        password = password
                    )
            ) {

                is LoginResult.Success -> {

                    try {
                        sessionManager.saveAuthenticatedSession(
                            userId = result.user.id,
                            authToken = result.token
                        )

                        restoreScheduledReminders(
                            userId =
                                result.user.id
                        )

                        _uiState.value =
                            AuthUiState.LoginSuccess(
                                userId =
                                    result.user.id
                            )

                    } catch (_: Exception) {
                        _uiState.value =
                            AuthUiState.Error
                    }
                }

                LoginResult.InvalidCredentials -> {
                    _uiState.value =
                        AuthUiState.InvalidCredentials
                }

                LoginResult.Error -> {
                    _uiState.value =
                        AuthUiState.Error
                }
            }
        }
    }

    fun logout() {

        viewModelScope.launch {

            val session =
                sessionManager
                    .sessionState
                    .first()

            val userId =
                session.userId

            if (userId != null) {

                try {

                    val scheduledReminders =
                        reminderRepository
                            .getScheduledReminders(
                                userId = userId
                            )

                    scheduledReminders
                        .forEach { reminder ->

                            try {

                                reminderScheduler
                                    .cancel(
                                        reminderId =
                                            reminder.id,

                                        userId =
                                            userId
                                    )

                            } catch (_: Exception) {
                            }
                        }

                } catch (_: Exception) {
                }
            }

            sessionManager.clearSession()

            _uiState.value =
                AuthUiState.LogoutSuccess
        }
    }

    private suspend fun restoreScheduledReminders(
        userId: Long
    ) {

        try {

            val scheduledReminders =
                reminderRepository
                    .getScheduledReminders(
                        userId = userId
                    )

            val currentTime =
                System.currentTimeMillis()

            scheduledReminders
                .forEach { reminder ->
                    if (
                        reminder.triggerAtMillis >
                        currentTime
                    ) {

                        reminderScheduler
                            .schedule(
                                reminder = reminder
                            )

                    } else {
                        reminderRepository
                            .cancelReminder(
                                reminderId =
                                    reminder.id,

                                userId =
                                    userId
                            )
                    }
                }

        } catch (_: Exception) {
        }
    }

    fun resetState() {

        _uiState.value =
            AuthUiState.Idle
    }

    companion object {

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {

                initializer {

                    val application =
                        this[APPLICATION_KEY]
                                as RachaProApplication

                    AuthViewModel(
                        userRepository =
                            application.userRepository,

                        sessionManager =
                            application.sessionManager,

                        reminderRepository =
                            application.reminderRepository,

                        reminderScheduler =
                            application.reminderScheduler
                    )
                }
            }
    }

}

sealed interface AuthUiState {

    data object Idle :
        AuthUiState

    data object Loading :
        AuthUiState

    data class RegistrationSuccess(
        val userId: Long
    ) : AuthUiState

    data object EmailAlreadyRegistered :
        AuthUiState

    data class LoginSuccess(
        val userId: Long
    ) : AuthUiState

    data object InvalidCredentials :
        AuthUiState

    data class ValidationError(
        val message: String
    ) : AuthUiState

    data object Error :
        AuthUiState

    data object LogoutSuccess :
        AuthUiState

}