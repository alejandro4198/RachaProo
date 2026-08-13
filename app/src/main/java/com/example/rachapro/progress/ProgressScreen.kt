package com.example.rachapro.progress

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import java.time.LocalDate


@Composable
fun ProgressScreen(
    uiState: ProgressUiState,
    onRetry: () -> Unit,
    onPeriodSelected: (ProgressPeriod) -> Unit
) {

    when (uiState) {

        ProgressUiState.Loading -> {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                CircularProgressIndicator()

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "Cargando progreso..."
                )
            }
        }

        is ProgressUiState.Success -> {

            ProgressSuccessContent(
                state = uiState,
                onPeriodSelected = onPeriodSelected
            )
        }

        ProgressUiState.NoActiveSession -> {

            ProgressErrorContent(
                message = "No hay una sesión activa.",
                onRetry = onRetry
            )
        }

        ProgressUiState.Error -> {

            ProgressErrorContent(
                message = "No fue posible cargar tu progreso.",
                onRetry = onRetry
            )
        }
    }
}

@Composable
private fun ProgressSuccessContent(
    state: ProgressUiState.Success,
    onPeriodSelected: (ProgressPeriod) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ) {

        Text(
            text = "Progreso",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Tu avance y constancia en RachaPro",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected =
                    state.selectedPeriod ==
                            ProgressPeriod.TODAY,

                onClick = {
                    onPeriodSelected(
                        ProgressPeriod.TODAY
                    )
                },

                label = {
                    Text("Hoy")
                },

                modifier =
                    Modifier.weight(1f)
            )

            FilterChip(
                selected =
                    state.selectedPeriod ==
                            ProgressPeriod.WEEK,

                onClick = {
                    onPeriodSelected(
                        ProgressPeriod.WEEK
                    )
                },

                label = {
                    Text("Semana")
                },

                modifier =
                    Modifier.weight(1f)
            )

            FilterChip(
                selected =
                    state.selectedPeriod ==
                            ProgressPeriod.ALL,

                onClick = {
                    onPeriodSelected(
                        ProgressPeriod.ALL
                    )
                },

                label = {
                    Text("Total")
                },

                modifier =
                    Modifier.weight(1f)
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Rachas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            ProgressStatCard(
                modifier = Modifier.weight(1f),
                title = "🔥 Racha actual",
                value =
                    formatDays(
                        state.currentStreakDays
                    )
            )

            ProgressStatCard(
                modifier = Modifier.weight(1f),
                title = "🏆 Mejor racha",
                value =
                    formatDays(
                        state.bestStreakDays
                    )
            )
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Text(
            text = "Productividad",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        ProgressStatCard(
            modifier = Modifier.fillMaxWidth(),
            title = "✅ Actividades completadas",
            value = state.completedActivities.toString()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        ProgressStatCard(
            modifier = Modifier.fillMaxWidth(),
            title = "🍅 Pomodoros completados",
            value = state.completedPomodoros.toString()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        ProgressStatCard(
            modifier = Modifier.fillMaxWidth(),
            title = "⏱ Tiempo total de enfoque",
            value =
                formatFocusTime(
                    totalSeconds =
                        state.totalFocusSeconds
                )
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Esta semana",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        WeeklyProgressChart(
            days = state.weeklyDays
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Detalle semanal",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        WeeklyProgressDetails(
            days = state.weeklyDays
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

@Composable
private fun ProgressStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {

    Card(
        modifier = modifier
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Text(
                text = title,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProgressErrorContent(
    message: String,
    onRetry: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {

        Text(
            text = message
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = onRetry
        ) {

            Text(
                text = "Reintentar"
            )
        }
    }
}

@Composable
private fun WeeklyProgressChart(
    days: List<WeeklyProgressDay>
) {

    val maxValue =
        days
            .maxOfOrNull { day ->
                day.completedActivities +
                        day.completedPomodoros
            }
            ?.coerceAtLeast(1)
            ?: 1

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Actividad diaria",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),

                horizontalArrangement =
                    Arrangement.SpaceEvenly,

                verticalAlignment =
                    Alignment.Bottom
            ) {

                days.forEach { day ->

                    val total =
                        day.completedActivities +
                                day.completedPomodoros

                    val proportion =
                        total.toFloat() /
                                maxValue.toFloat()

                    val barHeight =
                        if (total == 0) {
                            8.dp
                        } else {
                            20.dp +
                                    (100.dp * proportion)
                        }

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.Bottom
                    ) {

                        Text(
                            text = total.toString(),
                            fontSize = 12.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(barHeight)
                                .background(
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .primary,

                                    shape =
                                        RoundedCornerShape(
                                            topStart = 6.dp,
                                            topEnd = 6.dp
                                        )
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                dayLabel(
                                    day.epochDay
                                ),

                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    "Cada barra suma actividades y Pomodoros completados.",
                style =
                    MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun dayLabel(
    epochDay: Long
): String {

    return when (
        LocalDate
            .ofEpochDay(epochDay)
            .dayOfWeek.value
    ) {

        1 -> "L"
        2 -> "M"
        3 -> "X"
        4 -> "J"
        5 -> "V"
        6 -> "S"
        7 -> "D"

        else -> ""
    }
}

@Composable
private fun WeeklyProgressDetails(
    days: List<WeeklyProgressDay>
) {

    days.forEach { day ->

        val date =
            LocalDate.ofEpochDay(
                day.epochDay
            )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = dayName(
                        date.dayOfWeek.value
                    ),
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text =
                        "✅ ${day.completedActivities} actividades"
                )

                Text(
                    text =
                        "🍅 ${day.completedPomodoros} Pomodoros"
                )

                Text(
                    text =
                        "⏱ ${
                            formatFocusTime(
                                day.focusSeconds
                            )
                        } de enfoque"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )
    }
}

private fun dayName(
    dayOfWeek: Int
): String {

    return when (dayOfWeek) {

        1 -> "Lunes"
        2 -> "Martes"
        3 -> "Miércoles"
        4 -> "Jueves"
        5 -> "Viernes"
        6 -> "Sábado"
        7 -> "Domingo"

        else -> ""
    }
}

private fun formatFocusTime(
    totalSeconds: Long
): String {

    val totalMinutes =
        totalSeconds / 60L

    val hours =
        totalMinutes / 60L

    val minutes =
        totalMinutes % 60L

    return when {

        hours > 0L &&
                minutes > 0L -> {

            "$hours h $minutes min"
        }

        hours > 0L -> {

            "$hours h"
        }

        else -> {

            "$minutes min"
        }
    }
}

private fun formatDays(
    days: Int
): String {

    return if (days == 1) {
        "1 día"
    } else {
        "$days días"
    }
}