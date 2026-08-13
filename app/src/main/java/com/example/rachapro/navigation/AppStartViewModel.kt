package com.example.rachapro.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.rachapro.RachaProApplication
import com.example.rachapro.data.local.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppStartViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    val uiState: StateFlow<AppStartState> =
        sessionManager.sessionState
            .map { session ->

                when {

                    session.userId != null -> {

                        AppStartState.LoggedIn(
                            userId = session.userId
                        )
                    }

                    session.onboardingCompleted -> {

                        AppStartState.LoggedOut
                    }

                    else -> {

                        AppStartState.NeedsOnboarding
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    stopTimeoutMillis = 5_000
                ),
                initialValue = AppStartState.Loading
            )

    fun completeOnboarding() {

        viewModelScope.launch {

            sessionManager.setOnboardingCompleted(
                completed = true
            )
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {

                initializer {

                    val application =
                        this[APPLICATION_KEY]
                                as RachaProApplication

                    AppStartViewModel(
                        sessionManager =
                            application.sessionManager
                    )
                }
            }
    }
}

sealed interface AppStartState {

    data object Loading :
        AppStartState

    data object NeedsOnboarding :
        AppStartState

    data object LoggedOut :
        AppStartState

    data class LoggedIn(
        val userId: Long
    ) : AppStartState
}