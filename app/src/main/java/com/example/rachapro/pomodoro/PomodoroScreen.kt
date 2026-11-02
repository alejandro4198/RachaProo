package com.example.rachapro.pomodoro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.data.local.PomodoroPreferences
import com.example.rachapro.data.local.entity.PomodoroSessionStatus
import com.example.rachapro.data.local.entity.PomodoroSessionType
import com.example.rachapro.ui.components.PomodoroTimerRing
import com.example.rachapro.ui.components.RachaBrandGradient
import com.example.rachapro.ui.components.RachaCard
import com.example.rachapro.ui.components.RachaErrorMessage
import com.example.rachapro.ui.components.RachaGradientHeader
import com.example.rachapro.ui.components.RachaLoadingScreen
import com.example.rachapro.ui.components.RachaPrimaryButton
import com.example.rachapro.ui.components.RachaSecondaryButton
import com.example.rachapro.ui.components.RachaStatusBadge
import com.example.rachapro.ui.components.StatusBadgeType
import com.example.rachapro.ui.theme.RachaCyan
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted
import com.example.rachapro.ui.theme.RachaSurface

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
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        RachaGradientHeader(
            title = "Pomodoro",
            eyebrow = "Concentración",
            subtitle = "Enfócate, descansa y mantén tu racha."
        )

        Spacer(modifier = Modifier.height(20.dp))

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
                        initialScale = 0.94f,
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
                RachaCard {
                    Text(
                        text = "Debes iniciar sesión para usar Pomodoro.",
                        color = RachaOnSurfaceMuted
                    )
                }
            }

            is PomodoroUiState.Error -> {
                RachaErrorMessage(message = uiState.message)
            }
        }

        if (actionState is PomodoroActionState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            RachaErrorMessage(message = actionState.message)
        }

        Spacer(modifier = Modifier.height(24.dp))
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
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                    label = {
                        Text(
                            text = label,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.small,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RachaIndigo.copy(alpha = 0.12f),
                        selectedLabelColor = RachaIndigo
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val durationMillis = durationForType(selectedType, preferences)
        val minutes = (durationMillis / 60000L).toInt()

        RachaCard(
            containerColor = RachaIndigo.copy(alpha = 0.04f),
            border = null,
            contentPadding = 22.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (selectedType == PomodoroSessionType.FOCUS) RachaIndigo else RachaCyan)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = modeNameForType(selectedType),
                    style = MaterialTheme.typography.titleMedium,
                    color = RachaOnSurfaceMuted
                )

                Spacer(modifier = Modifier.height(18.dp))

                PomodoroTimerRing(
                    remainingMillis = durationMillis,
                    totalMillis = durationMillis,
                    label = "minutos"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$minutes minutos",
                    style = MaterialTheme.typography.labelLarge,
                    color = RachaIndigo
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when (selectedType) {
                        PomodoroSessionType.FOCUS -> "Silencia las notificaciones y empieza."
                        PomodoroSessionType.SHORT_BREAK -> "Respira y estira la vista un momento."
                        else -> "Recarga energía antes de la siguiente ronda."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = RachaOnSurfaceMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val startLabel =
            when (selectedType) {
                PomodoroSessionType.SHORT_BREAK -> "Iniciar descanso corto"
                PomodoroSessionType.LONG_BREAK -> "Iniciar descanso largo"
                else -> "Iniciar enfoque"
            }

        RachaPrimaryButton(
            text = if (actionState is PomodoroActionState.Starting) {
                "Iniciando temporizador..."
            } else {
                startLabel
            },
            onClick = {
                when (selectedType) {
                    PomodoroSessionType.FOCUS -> onStartFocus()
                    PomodoroSessionType.SHORT_BREAK -> onStartShortBreak()
                    PomodoroSessionType.LONG_BREAK -> onStartLongBreak()
                    else -> onStartFocus()
                }
            },
            enabled = actionState !is PomodoroActionState.Starting,
            icon = Icons.Filled.PlayArrow
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surface)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Timer,
                contentDescription = null,
                tint = RachaCyan,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Tu configuración: ${preferences.focusMinutes} min de enfoque · ${preferences.shortBreakMinutes} min corto · ${preferences.longBreakMinutes} min largo.",
                style = MaterialTheme.typography.bodySmall,
                color = RachaOnSurfaceMuted
            )
        }
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
    val isRunning = session.status == PomodoroSessionStatus.RUNNING

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RachaCard(
            containerColor = RachaIndigo.copy(alpha = 0.04f),
            border = null,
            contentPadding = 22.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RachaStatusBadge(
                    text = if (isRunning) "En curso" else "Pausado",
                    type = if (isRunning) StatusBadgeType.Success else StatusBadgeType.Warning
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = modeNameForType(session.type),
                    style = MaterialTheme.typography.titleMedium,
                    color = RachaOnSurfaceMuted
                )

                Spacer(modifier = Modifier.height(18.dp))

                PomodoroTimerRing(
                    remainingMillis = state.remainingMillis,
                    totalMillis = totalMillis,
                    isActive = isRunning
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isRunning) {
                        "Mantén el ritmo. Tú puedes."
                    } else {
                        "Tómate un segundo y retoma cuando estés listo."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = RachaOnSurfaceMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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

                else -> Unit
            }

            RachaSecondaryButton(
                text = if (actionState is PomodoroActionState.Cancelling) "Cancelando..." else "Cancelar",
                onClick = onCancel,
                enabled = actionState !is PomodoroActionState.Cancelling,
                modifier = Modifier.weight(1f)
            )
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

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RachaCard(
            containerColor = RachaIndigo.copy(alpha = 0.04f),
            border = null,
            contentPadding = 24.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(RachaBrandGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎉", fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (state.recommendedType) {
                    PomodoroSessionType.SHORT_BREAK -> {
                        Text(
                            text = "¡Enfoque #${state.completedFocusCount ?: 0} completado!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Descanso corto recomendado · ${prefs.shortBreakMinutes} min",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RachaOnSurfaceMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        RachaPrimaryButton(
                            text = "Iniciar descanso",
                            onClick = onStartShortBreak,
                            icon = Icons.Filled.PlayArrow
                        )
                    }

                    PomodoroSessionType.LONG_BREAK -> {
                        Text(
                            text = "¡Enfoque #${state.completedFocusCount ?: 0} completado!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Descanso largo recomendado · ${prefs.longBreakMinutes} min",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RachaOnSurfaceMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        RachaPrimaryButton(
                            text = "Iniciar descanso largo",
                            onClick = onStartLongBreak,
                            icon = Icons.Filled.PlayArrow
                        )
                    }

                    else -> {
                        Text(
                            text = "¡Sesión completada!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Listo para una nueva sesión de enfoque.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RachaOnSurfaceMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        RachaPrimaryButton(
                            text = "Iniciar enfoque",
                            onClick = onStartFocus,
                            icon = Icons.Filled.PlayArrow
                        )
                    }
                }
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
