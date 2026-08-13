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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import com.example.rachapro.ui.components.RachaGradientHeader
import com.example.rachapro.ui.components.RachaLoadingScreen
import com.example.rachapro.ui.components.RachaStatCard
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.rachapro.ui.theme.RachaCyan
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaMint
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted
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
                RachaLoadingScreen("Cargando progreso...")
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
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        RachaGradientHeader(
            title = "Progreso",
            subtitle = "Tu avance y constancia en RachaPro"
        )

        Spacer(modifier = Modifier.height(20.dp))

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

                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RachaIndigo.copy(alpha = 0.15f),
                    selectedLabelColor = RachaIndigo
                )
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
                    Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RachaIndigo.copy(alpha = 0.15f),
                    selectedLabelColor = RachaIndigo
                )
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
                    Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RachaIndigo.copy(alpha = 0.15f),
                    selectedLabelColor = RachaIndigo
                )
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

            RachaStatCard(
                modifier = Modifier.weight(1f),
                emoji = "🔥",
                label = "Racha actual",
                value = formatDays(state.currentStreakDays),
                accentColor = RachaIndigo
            )

            RachaStatCard(
                modifier = Modifier.weight(1f),
                emoji = "🏆",
                label = "Mejor racha",
                value = formatDays(state.bestStreakDays),
                accentColor = RachaCyan
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

        Spacer(modifier = Modifier.height(24.dp))

        ProductivitySummaryChart(
            activities = state.completedActivities,
            pomodoros = state.completedPomodoros,
            focusSeconds = state.totalFocusSeconds
        )

        Spacer(modifier = Modifier.height(24.dp))

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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tiempo de enfoque",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        FocusTimeTrendChart(days = state.weeklyDays)

        Spacer(modifier = Modifier.height(20.dp))

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
private fun ProductivitySummaryChart(
    activities: Int,
    pomodoros: Int,
    focusSeconds: Long
) {
    val total = (activities + pomodoros).coerceAtLeast(1)
    val activitiesAngle = 360f * activities / total
    val pomodorosAngle = 360f * pomodoros / total

    val animatedActivities by animateFloatAsState(
        targetValue = activitiesAngle,
        animationSpec = tween(800),
        label = "activitiesAngle"
    )
    val animatedPomodoros by animateFloatAsState(
        targetValue = pomodorosAngle,
        animationSpec = tween(800),
        label = "pomodorosAngle"
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Resumen de productividad", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        val stroke = 16.dp.toPx()
                        val diameter = size.minDimension - stroke
                        val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
                        val arcSize = Size(diameter, diameter)

                        drawArc(
                            color = RachaIndigo.copy(alpha = 0.15f),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(stroke, cap = StrokeCap.Round)
                        )

                        if (activities > 0) {
                            drawArc(
                                color = RachaIndigo,
                                startAngle = -90f,
                                sweepAngle = animatedActivities,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(stroke, cap = StrokeCap.Round)
                            )
                        }

                        if (pomodoros > 0) {
                            drawArc(
                                color = RachaCyan,
                                startAngle = -90f + animatedActivities,
                                sweepAngle = animatedPomodoros,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(stroke, cap = StrokeCap.Round)
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$activities", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(text = "act.", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LegendItem(color = RachaIndigo, label = "Actividades", value = activities.toString())
                    LegendItem(color = RachaCyan, label = "Pomodoros", value = pomodoros.toString())
                    LegendItem(color = RachaMint, label = "Enfoque", value = formatFocusTime(focusSeconds))
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: androidx.compose.ui.graphics.Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(3.dp))
        )
        Text(text = "$label: $value", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun WeeklyProgressChart(
    days: List<WeeklyProgressDay>
) {

    val maxActivities = days.maxOfOrNull { it.completedActivities }?.coerceAtLeast(1) ?: 1
    val maxPomodoros = days.maxOfOrNull { it.completedPomodoros }?.coerceAtLeast(1) ?: 1
    val maxValue = maxOf(maxActivities, maxPomodoros)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(text = "Actividad semanal", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendItem(color = RachaIndigo, label = "Actividades", value = "")
                LegendItem(color = RachaCyan, label = "Pomodoros", value = "")
            }

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

                    val actProp = day.completedActivities.toFloat() / maxValue
                    val pomProp = day.completedPomodoros.toFloat() / maxValue

                    val actHeight = if (day.completedActivities == 0) 4.dp else 12.dp + (90.dp * actProp)
                    val pomHeight = if (day.completedPomodoros == 0) 4.dp else 12.dp + (90.dp * pomProp)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(10.dp)
                                    .height(actHeight)
                                    .background(RachaIndigo, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .width(10.dp)
                                    .height(pomHeight)
                                    .background(RachaCyan, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = dayLabel(day.epochDay), fontWeight = FontWeight.Medium, fontSize = 12.sp)
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Barras comparativas por día de la semana.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun FocusTimeTrendChart(
    days: List<WeeklyProgressDay>
) {
    val maxFocus = days.maxOfOrNull { it.focusSeconds }?.coerceAtLeast(1L) ?: 1L
    val totalFocus = days.sumOf { it.focusSeconds }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Minutos de enfoque", fontWeight = FontWeight.SemiBold)
                Text(
                    text = formatFocusTime(totalFocus),
                    style = MaterialTheme.typography.labelLarge,
                    color = RachaIndigo
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEach { day ->
                    val proportion = day.focusSeconds.toFloat() / maxFocus.toFloat()
                    val barHeight = if (day.focusSeconds == 0L) 6.dp else 16.dp + (100.dp * proportion)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        if (day.focusSeconds > 0) {
                            Text(
                                text = "${day.focusSeconds / 60}",
                                fontSize = 10.sp,
                                color = RachaOnSurfaceMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height(barHeight)
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(RachaCyan, RachaIndigo)
                                    ),
                                    shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = dayLabel(day.epochDay), fontWeight = FontWeight.Medium, fontSize = 12.sp)
                    }
                }
            }
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