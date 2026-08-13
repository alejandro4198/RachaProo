package com.example.rachapro.pomodoro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.data.local.entity.PomodoroSessionStatus
import com.example.rachapro.data.local.entity.PomodoroSessionType
import kotlin.math.ceil
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue


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
) {

    var selectedType by rememberSaveable {
        mutableStateOf(
            PomodoroSessionType.FOCUS
        )
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Text(
            text = "Pomodoro",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        when (uiState) {

            PomodoroUiState.Loading -> {

                CircularProgressIndicator()

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "Cargando..."
                )
            }

            PomodoroUiState.Idle -> {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    FilterChip(
                        selected =
                            selectedType ==
                                    PomodoroSessionType.FOCUS,

                        onClick = {
                            selectedType =
                                PomodoroSessionType.FOCUS
                        },

                        label = {
                            Text("Enfoque")
                        },

                        modifier =
                            Modifier.weight(1f)
                    )

                    FilterChip(
                        selected =
                            selectedType ==
                                    PomodoroSessionType.SHORT_BREAK,

                        onClick = {
                            selectedType =
                                PomodoroSessionType.SHORT_BREAK
                        },

                        label = {
                            Text("Corto")
                        },

                        modifier =
                            Modifier.weight(1f)
                    )

                    FilterChip(
                        selected =
                            selectedType ==
                                    PomodoroSessionType.LONG_BREAK,

                        onClick = {
                            selectedType =
                                PomodoroSessionType.LONG_BREAK
                        },

                        label = {
                            Text("Largo")
                        },

                        modifier =
                            Modifier.weight(1f)
                    )
                }

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                val idleDurationMillis =
                    when (selectedType) {

                        PomodoroSessionType.FOCUS ->
                            25L * 60L * 1000L

                        PomodoroSessionType.SHORT_BREAK ->
                            5L * 60L * 1000L

                        PomodoroSessionType.LONG_BREAK ->
                            15L * 60L * 1000L

                        else ->
                            25L * 60L * 1000L
                    }

                val modeName =
                    when (selectedType) {

                        PomodoroSessionType.FOCUS ->
                            "Enfoque"

                        PomodoroSessionType.SHORT_BREAK ->
                            "Descanso corto"

                        PomodoroSessionType.LONG_BREAK ->
                            "Descanso largo"

                        else ->
                            "Pomodoro"
                    }

                TimerText(
                    remainingMillis =
                        idleDurationMillis
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = modeName,
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                Button(
                    onClick = {

                        when (selectedType) {

                            PomodoroSessionType.FOCUS ->
                                onStartFocus()

                            PomodoroSessionType.SHORT_BREAK ->
                                onStartShortBreak()

                            PomodoroSessionType.LONG_BREAK ->
                                onStartLongBreak()
                        }
                    },

                    enabled =
                        actionState
                                !is PomodoroActionState.Starting
                ) {

                    Text(
                        text =
                            if (
                                actionState
                                        is PomodoroActionState.Starting
                            ) {
                                "Iniciando..."
                            } else {
                                "Iniciar"
                            }
                    )
                }
            }

            is PomodoroUiState.Active -> {

                val session =
                    uiState.session

                TimerText(
                    remainingMillis =
                        uiState.remainingMillis
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        when (session.type) {

                            PomodoroSessionType.FOCUS ->
                                "Enfoque"

                            PomodoroSessionType.SHORT_BREAK ->
                                "Descanso corto"

                            PomodoroSessionType.LONG_BREAK ->
                                "Descanso largo"

                            else ->
                                "Pomodoro"
                        },

                    style =
                        MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        when (session.status) {

                            PomodoroSessionStatus.RUNNING ->
                                "En curso"

                            PomodoroSessionStatus.PAUSED ->
                                "Pausado"

                            else ->
                                session.status
                        }
                )

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            12.dp,
                            Alignment.CenterHorizontally
                        )
                ) {

                    when (session.status) {

                        PomodoroSessionStatus.RUNNING -> {

                            Button(
                                onClick = onPause,
                                enabled =
                                    actionState
                                            !is PomodoroActionState.Pausing
                            ) {

                                Text(
                                    text =
                                        if (
                                            actionState
                                                    is PomodoroActionState.Pausing
                                        ) {
                                            "Pausando..."
                                        } else {
                                            "Pausar"
                                        }
                                )
                            }
                        }

                        PomodoroSessionStatus.PAUSED -> {

                            Button(
                                onClick = onResume,
                                enabled =
                                    actionState
                                            !is PomodoroActionState.Resuming
                            ) {

                                Text(
                                    text =
                                        if (
                                            actionState
                                                    is PomodoroActionState.Resuming
                                        ) {
                                            "Reanudando..."
                                        } else {
                                            "Reanudar"
                                        }
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onCancel,
                        enabled =
                            actionState
                                    !is PomodoroActionState.Cancelling
                    ) {

                        Text(
                            text =
                                if (
                                    actionState
                                            is PomodoroActionState.Cancelling
                                ) {
                                    "Cancelando..."
                                } else {
                                    "Cancelar"
                                }
                        )
                    }
                }
            }

            is PomodoroUiState.Completed -> {

                Text(
                    text = "00:00",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                when (uiState.recommendedType) {

                    PomodoroSessionType.SHORT_BREAK -> {

                        Text(
                            text =
                                "¡Enfoque #${uiState.completedFocusCount ?: 0} completado! 🎉",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Siguiente recomendado:"
                        )

                        Text(
                            text = "Descanso corto · 5 min",
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        Button(
                            onClick = onStartShortBreak
                        ) {
                            Text(
                                text = "Iniciar descanso"
                            )
                        }
                    }

                    PomodoroSessionType.LONG_BREAK -> {

                        Text(
                            text =
                                "¡Enfoque #${uiState.completedFocusCount ?: 0} completado! 🎉",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Siguiente recomendado:"
                        )

                        Text(
                            text = "Descanso largo · 15 min",
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        Button(
                            onClick = onStartLongBreak
                        ) {
                            Text(
                                text = "Iniciar descanso largo"
                            )
                        }
                    }

                    else -> {

                        Text(
                            text = "¡Descanso completado! ☕",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Puedes comenzar una nueva sesión de enfoque."
                        )

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        Button(
                            onClick = onStartFocus
                        ) {
                            Text(
                                text = "Iniciar enfoque"
                            )
                        }
                    }
                }
            }

            PomodoroUiState.NoActiveSession -> {

                Text(
                    text =
                        "Debes iniciar sesión para usar Pomodoro."
                )
            }

            is PomodoroUiState.Error -> {

                Text(
                    text = uiState.message,
                    color =
                        MaterialTheme.colorScheme.error
                )
            }
        }

        if (
            actionState
                    is PomodoroActionState.Error
        ) {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = actionState.message,
                color =
                    MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun TimerText(
    remainingMillis: Long
) {

    val totalSeconds =
        ceil(
            remainingMillis / 1000.0
        )
            .toLong()
            .coerceAtLeast(0L)

    val minutes =
        totalSeconds / 60

    val seconds =
        totalSeconds % 60

    Text(
        text =
            String.format(
                "%02d:%02d",
                minutes,
                seconds
            ),

        fontSize = 64.sp,
        fontWeight = FontWeight.Bold
    )
}