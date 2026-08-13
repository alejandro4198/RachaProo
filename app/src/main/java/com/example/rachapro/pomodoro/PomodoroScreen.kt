package com.example.rachapro.pomodoro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rachapro.data.local.PomodoroPreferences
import com.example.rachapro.data.local.entity.PomodoroSessionStatus
import com.example.rachapro.data.local.entity.PomodoroSessionType
import com.example.rachapro.ui.components.PomodoroTimerRing
import com.example.rachapro.ui.components.RachaGradientHeader
import com.example.rachapro.ui.components.RachaLoadingScreen
import com.example.rachapro.ui.components.RachaPrimaryButton
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted

@Composable
fun PomodoroScreen(
    uiState: PomodoroUiState,
    actionState: PomodoroActionState,
    onStartFocus: () -> Unit,
    onStartShortBreak: () -> Unit,
    onStartLongBreak: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onDismissCompleted: () -> Unit = {},
) {
    var selectedType by rememberSaveable {
        mutableStateOf(PomodoroSessionType.FOCUS)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RachaGradientHeader(
            title = "Pomodoro",
            subtitle = "Enfócate, descansa y mantén tu racha"
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (uiState) {
            PomodoroUiState.Loading -> {
                RachaLoadingScreen("Preparando temporizador...")
            }

            is PomodoroUiState.Idle -> {
                IdleContent(
                    preferences = uiState.preferences,
                    selectedType = selectedType,
                    actionState = actionState,
                    onTypeSelected = { selectedType = it },
                    onStartFocus = onStartFocus,
                    onStartShortBreak = onStartShortBreak,
                    onStartLongBreak = onStartLongBreak
                )
            }

            is PomodoroUiState.Active -> {
                ActiveContent(
                    state = uiState,
                    actionState = actionState,
                    onPause = onPause,
                    onResume = onResume,
                    onCancel = onCancel
                )
            }

            is PomodoroUiState.Completed -> {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(400)) + scaleIn(
                        initialScale = 0.9f,
                        animationSpec = tween(400)
                    )
                ) {
                    CompletedContent(
                        state = uiState,
                        onStartFocus = {
                            onDismissCompleted()
                            onStartFocus()
                        },
                        onStartShortBreak = {
                            onDismissCompleted()
                            onStartShortBreak()
                        },
                        onStartLongBreak = {
                            onDismissCompleted()
                            onStartLongBreak()
                        }
                    )
                }
            }

            PomodoroUiState.NoActiveSession -> {
                Text(
                    text = "Debes iniciar sesión para usar Pomodoro.",
                    color = RachaOnSurfaceMuted
                )
            }

            is PomodoroUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        if (actionState is PomodoroActionState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = actionState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun IdleContent(
    preferences: PomodoroPreferences,
    selectedType: String,
    actionState: PomodoroActionState,
    onTypeSelected: (String) -> Unit,
    onStartFocus: () -> Unit,
    onStartShortBreak: () -> Unit,
    onStartLongBreak: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                PomodoroSessionType.FOCUS to "Enfoque",
                PomodoroSessionType.SHORT_BREAK to "Corto",
                PomodoroSessionType.LONG_BREAK to "Largo"
            ).forEach { (type, label) ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(label) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RachaIndigo.copy(alpha = 0.15f),
                        selectedLabelColor = RachaIndigo
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        val durationMillis = durationForType(selectedType, preferences)

        PomodoroTimerRing(
            remainingMillis = durationMillis,
            totalMillis = durationMillis
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = modeNameForType(selectedType),
            style = MaterialTheme.typography.titleMedium,
            color = RachaOnSurfaceMuted
        )

        Spacer(modifier = Modifier.height(28.dp))

        RachaPrimaryButton(
            text = if (actionState is PomodoroActionState.Starting) {
                "Iniciando..."
            } else {
                "Iniciar sesión"
            },
            onClick = {
                when (selectedType) {
                    PomodoroSessionType.FOCUS -> onStartFocus()
                    PomodoroSessionType.SHORT_BREAK -> onStartShortBreak()
                    PomodoroSessionType.LONG_BREAK -> onStartLongBreak()
                    else -> onStartFocus()
                }
            },
            enabled = actionState !is PomodoroActionState.Starting
        )
    }
}

@Composable
private fun ActiveContent(
    state: PomodoroUiState.Active,
    actionState: PomodoroActionState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit
) {
    val session = state.session
    val totalMillis = session.plannedDurationSeconds * 1000L

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        PomodoroTimerRing(
            remainingMillis = state.remainingMillis,
            totalMillis = totalMillis,
            isActive = session.status == PomodoroSessionStatus.RUNNING
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = modeNameForType(session.type), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = when (session.status) {
                PomodoroSessionStatus.RUNNING -> "En curso"
                PomodoroSessionStatus.PAUSED -> "Pausado"
                else -> session.status
            },
            style = MaterialTheme.typography.bodyMedium,
            color = RachaOnSurfaceMuted
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            when (session.status) {
                PomodoroSessionStatus.RUNNING -> {
                    RachaPrimaryButton(
                        text = if (actionState is PomodoroActionState.Pausing) "Pausando..." else "Pausar",
                        onClick = onPause,
                        enabled = actionState !is PomodoroActionState.Pausing,
                        modifier = Modifier.weight(1f)
                    )
                }
                PomodoroSessionStatus.PAUSED -> {
                    RachaPrimaryButton(
                        text = if (actionState is PomodoroActionState.Resuming) "Reanudando..." else "Reanudar",
                        onClick = onResume,
                        enabled = actionState !is PomodoroActionState.Resuming,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            OutlinedButton(
                onClick = onCancel,
                enabled = actionState !is PomodoroActionState.Cancelling,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (actionState is PomodoroActionState.Cancelling) "Cancelando..." else "Cancelar"
                )
            }
        }
    }
}

@Composable
private fun CompletedContent(
    state: PomodoroUiState.Completed,
    onStartFocus: () -> Unit,
    onStartShortBreak: () -> Unit,
    onStartLongBreak: () -> Unit
) {
    val prefs = state.preferences

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        PomodoroTimerRing(remainingMillis = 0L, totalMillis = 1L)

        Spacer(modifier = Modifier.height(16.dp))

        when (state.recommendedType) {
            PomodoroSessionType.SHORT_BREAK -> {
                Text(
                    text = "¡Enfoque #${state.completedFocusCount ?: 0} completado! 🎉",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Descanso corto recomendado · ${prefs.shortBreakMinutes} min",
                    color = RachaOnSurfaceMuted
                )
                Spacer(modifier = Modifier.height(20.dp))
                RachaPrimaryButton(text = "Iniciar descanso", onClick = onStartShortBreak)
            }

            PomodoroSessionType.LONG_BREAK -> {
                Text(
                    text = "¡Enfoque #${state.completedFocusCount ?: 0} completado! 🎉",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Descanso largo recomendado · ${prefs.longBreakMinutes} min",
                    color = RachaOnSurfaceMuted
                )
                Spacer(modifier = Modifier.height(20.dp))
                RachaPrimaryButton(text = "Iniciar descanso largo", onClick = onStartLongBreak)
            }

            else -> {
                Text(
                    text = "¡Sesión completada! ☕",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Listo para una nueva sesión de enfoque.", color = RachaOnSurfaceMuted)
                Spacer(modifier = Modifier.height(20.dp))
                RachaPrimaryButton(text = "Iniciar enfoque", onClick = onStartFocus)
            }
        }
    }
}

private fun durationForType(type: String, preferences: PomodoroPreferences): Long {
    val minutes = when (type) {
        PomodoroSessionType.FOCUS -> preferences.focusMinutes
        PomodoroSessionType.SHORT_BREAK -> preferences.shortBreakMinutes
        PomodoroSessionType.LONG_BREAK -> preferences.longBreakMinutes
        else -> preferences.focusMinutes
    }
    return minutes.toLong() * 60L * 1000L
}

private fun modeNameForType(type: String): String {
    return when (type) {
        PomodoroSessionType.FOCUS -> "Modo enfoque"
        PomodoroSessionType.SHORT_BREAK -> "Descanso corto"
        PomodoroSessionType.LONG_BREAK -> "Descanso largo"
        else -> "Pomodoro"
    }
}
