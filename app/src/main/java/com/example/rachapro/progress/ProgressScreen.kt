package com.example.rachapro.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.ui.components.RachaCard
import com.example.rachapro.ui.components.RachaEmptyState
import com.example.rachapro.ui.components.RachaGradientHeader
import com.example.rachapro.ui.components.RachaLoadingScreen
import com.example.rachapro.ui.components.RachaPrimaryButton
import com.example.rachapro.ui.components.RachaProgressRing
import com.example.rachapro.ui.components.RachaSectionHeader
import com.example.rachapro.ui.components.RachaStatCard
import com.example.rachapro.ui.theme.RachaCyan
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaMint
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted
import com.example.rachapro.ui.theme.RachaStreak
import com.example.rachapro.ui.theme.RachaSurface
import com.example.rachapro.ui.theme.RachaWarning
import java.time.LocalDate

@Composable
fun ProgressScreen(
    uiState: ProgressUiState,
    onRetry: () -> Unit,
    onPeriodSelected: (ProgressPeriod) -> Unit
) {

    when (uiState) {

        ProgressUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
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
    val weeklyActivities = state.weeklyDays.sumOf { it.completedActivities }
    val weeklyPomodoros = state.weeklyDays.sumOf { it.completedPomodoros }
    val bestDay = state.weeklyDays.maxByOrNull { it.focusSeconds }
    val hasActivity = state.completedActivities > 0 ||
        state.completedPomodoros > 0 ||
        state.totalFocusSeconds > 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        RachaGradientHeader(
            title = "Progreso",
            eyebrow = "Tu avance",
            subtitle = "Constancia, enfoque y pequeños logros.",
            trailing = {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(RachaSurface.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${state.currentStreakDays}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = RachaSurface
                        )
                        Text(
                            text = "racha",
                            style = MaterialTheme.typography.labelSmall,
                            color = RachaSurface.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PeriodChip(
                label = "Hoy",
                selected = state.selectedPeriod == ProgressPeriod.TODAY,
                modifier = Modifier.weight(1f),
                onClick = { onPeriodSelected(ProgressPeriod.TODAY) }
            )
            PeriodChip(
                label = "Semana",
                selected = state.selectedPeriod == ProgressPeriod.WEEK,
                modifier = Modifier.weight(1f),
                onClick = { onPeriodSelected(ProgressPeriod.WEEK) }
            )
            PeriodChip(
                label = "Total",
                selected = state.selectedPeriod == ProgressPeriod.ALL,
                modifier = Modifier.weight(1f),
                onClick = { onPeriodSelected(ProgressPeriod.ALL) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (!hasActivity) {
            RachaEmptyState(
                emoji = "🌱",
                title = "Tu progreso empieza aquí",
                description = "Completa una actividad o una sesión de enfoque para ver tus estadísticas."
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        RachaSectionHeader(
            title = "Resumen",
            supportingText = when (state.selectedPeriod) {
                ProgressPeriod.TODAY -> "Lo que has logrado hoy"
                ProgressPeriod.WEEK -> "Lo que has logrado esta semana"
                ProgressPeriod.ALL -> "Tu historial completo"
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        RachaCard(
            containerColor = RachaIndigo.copy(alpha = 0.04f),
            border = null
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RachaProgressRing(
                    progress = if (state.bestStreakDays > 0) {
                        state.currentStreakDays.toFloat() / state.bestStreakDays.toFloat()
                    } else {
                        0f
                    },
                    size = 108.dp,
                    strokeWidth = 11.dp,
                    valueText = "${state.currentStreakDays}",
                    labelText = "días",
                    ringColor = RachaIndigo
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Racha actual",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (state.currentStreakDays > 0) {
                            "Tu mejor racha es de ${formatDays(state.bestStreakDays)}."
                        } else {
                            "Completa una actividad hoy para encenderla."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = RachaOnSurfaceMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        MiniMetric(emoji = "✅", value = "${state.completedActivities}", label = "tareas")
                        MiniMetric(emoji = "🍅", value = "${state.completedPomodoros}", label = "pomos")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RachaStatCard(
                modifier = Modifier.weight(1f),
                emoji = "🔥",
                label = "Racha actual",
                value = formatDays(state.currentStreakDays),
                accentColor = RachaStreak
            )
            RachaStatCard(
                modifier = Modifier.weight(1f),
                emoji = "🏆",
                label = "Mejor racha",
                value = formatDays(state.bestStreakDays),
                accentColor = RachaWarning
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        RachaCard {
            Column {
                Text(
                    text = "⏱ Tiempo total de enfoque",
                    style = MaterialTheme.typography.labelLarge,
                    color = RachaOnSurfaceMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatFocusTime(state.totalFocusSeconds),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Cada minuto de concentración cuenta.",
                    style = MaterialTheme.typography.bodySmall,
                    color = RachaOnSurfaceMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        RachaSectionHeader(
            title = "Productividad",
            supportingText = "Actividades y pomodoros en el periodo"
        )

        Spacer(modifier = Modifier.height(12.dp))

        ProductivitySummaryChart(
            activities = state.completedActivities,
            pomodoros = state.completedPomodoros,
            focusSeconds = state.totalFocusSeconds
        )

        Spacer(modifier = Modifier.height(24.dp))

        RachaSectionHeader(
            title = "Esta semana",
            supportingText = if (weeklyActivities + weeklyPomodoros == 0) {
                "Aún sin registros esta semana"
            } else {
                "$weeklyActivities tareas · $weeklyPomodoros pomodoros"
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        WeeklyProgressChart(days = state.weeklyDays)

        Spacer(modifier = Modifier.height(24.dp))

        RachaSectionHeader(
            title = "Tiempo de enfoque",
            supportingText = bestDay?.takeIf { it.focusSeconds > 0 }?.let {
                "Tu mejor día: ${dayName(LocalDate.ofEpochDay(it.epochDay).dayOfWeek.value)}"
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        FocusTimeTrendChart(days = state.weeklyDays)

        Spacer(modifier = Modifier.height(24.dp))

        RachaSectionHeader(
            title = "Detalle semanal",
            supportingText = "Día a día, sin perder el ritmo"
        )

        Spacer(modifier = Modifier.height(12.dp))

        WeeklyProgressDetails(days = state.weeklyDays)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PeriodChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = RachaIndigo.copy(alpha = 0.12f),
            selectedLabelColor = RachaIndigo
        )
    )
}

@Composable
private fun MiniMetric(
    emoji: String,
    value: String,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = emoji, fontSize = 14.sp)
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = RachaOnSurfaceMuted
        )
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RachaCard {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "📊", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                RachaPrimaryButton(text = "Reintentar", onClick = onRetry)
            }
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

    RachaCard {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(124.dp)) {
                        val stroke = 16.dp.toPx()
                        val diameter = size.minDimension - stroke
                        val topLeft = Offset(
                            (size.width - diameter) / 2,
                            (size.height - diameter) / 2
                        )
                        val arcSize = Size(diameter, diameter)

                        drawArc(
                            color = RachaIndigo.copy(alpha = 0.12f),
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
                        Text(
                            text = "${activities + pomodoros}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "logros",
                            style = MaterialTheme.typography.labelSmall,
                            color = RachaOnSurfaceMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LegendItem(
                        color = RachaIndigo,
                        label = "Actividades",
                        value = activities.toString()
                    )
                    LegendItem(
                        color = RachaCyan,
                        label = "Pomodoros",
                        value = pomodoros.toString()
                    )
                    LegendItem(
                        color = RachaMint,
                        label = "Enfoque",
                        value = formatFocusTime(focusSeconds)
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = RachaOnSurfaceMuted,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun WeeklyProgressChart(
    days: List<WeeklyProgressDay>
) {
    val maxActivities = days.maxOfOrNull { it.completedActivities }?.coerceAtLeast(1) ?: 1
    val maxPomodoros = days.maxOfOrNull { it.completedPomodoros }?.coerceAtLeast(1) ?: 1
    val maxValue = maxOf(maxActivities, maxPomodoros)

    RachaCard {
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendItem(color = RachaIndigo, label = "Actividades", value = "")
                LegendItem(color = RachaCyan, label = "Pomodoros", value = "")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEach { day ->
                    val actProp = day.completedActivities.toFloat() / maxValue
                    val pomProp = day.completedPomodoros.toFloat() / maxValue

                    val actHeight = if (day.completedActivities == 0) 4.dp else 14.dp + (86.dp * actProp)
                    val pomHeight = if (day.completedPomodoros == 0) 4.dp else 14.dp + (86.dp * pomProp)

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
                                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                    .background(RachaIndigo)
                            )
                            Box(
                                modifier = Modifier
                                    .width(10.dp)
                                    .height(pomHeight)
                                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                    .background(RachaCyan)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = dayLabel(day.epochDay),
                            style = MaterialTheme.typography.labelMedium,
                            color = RachaOnSurfaceMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FocusTimeTrendChart(
    days: List<WeeklyProgressDay>
) {
    val maxFocus = days.maxOfOrNull { it.focusSeconds }?.coerceAtLeast(1L) ?: 1L
    val totalFocus = days.sumOf { it.focusSeconds }

    RachaCard {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Minutos de enfoque",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = formatFocusTime(totalFocus),
                    style = MaterialTheme.typography.labelLarge,
                    color = RachaIndigo
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEach { day ->
                    val proportion = day.focusSeconds.toFloat() / maxFocus.toFloat()
                    val barHeight = if (day.focusSeconds == 0L) 6.dp else 16.dp + (96.dp * proportion)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        if (day.focusSeconds > 0) {
                            Text(
                                text = "${day.focusSeconds / 60}",
                                style = MaterialTheme.typography.labelSmall,
                                color = RachaOnSurfaceMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(RachaCyan, RachaIndigo)
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = dayLabel(day.epochDay),
                            style = MaterialTheme.typography.labelMedium,
                            color = RachaOnSurfaceMuted
                        )
                    }
                }
            }
        }
    }
}

private fun dayLabel(epochDay: Long): String {
    return when (LocalDate.ofEpochDay(epochDay).dayOfWeek.value) {
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
    RachaCard {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            days.forEachIndexed { index, day ->
                val date = LocalDate.ofEpochDay(day.epochDay)
                val isToday = date == LocalDate.now()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isToday) RachaIndigo.copy(alpha = 0.06f) else Color.Transparent
                        )
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = dayName(date.dayOfWeek.value),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = if (isToday) "Hoy" else dateLabel(date),
                            style = MaterialTheme.typography.labelSmall,
                            color = RachaOnSurfaceMuted
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "✅ ${day.completedActivities}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "🍅 ${day.completedPomodoros}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = formatFocusTime(day.focusSeconds),
                            style = MaterialTheme.typography.labelMedium,
                            color = RachaIndigo
                        )
                    }
                }
                if (index < days.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }
        }
    }
}

private fun dayName(dayOfWeek: Int): String {
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

private fun dateLabel(date: LocalDate): String {
    val months = listOf(
        "ene", "feb", "mar", "abr", "may", "jun",
        "jul", "ago", "sep", "oct", "nov", "dic"
    )
    return "${date.dayOfMonth} ${months[date.monthValue - 1]}"
}

private fun formatFocusTime(totalSeconds: Long): String {
    val totalMinutes = totalSeconds / 60L
    val hours = totalMinutes / 60L
    val minutes = totalMinutes % 60L

    return when {
        hours > 0L && minutes > 0L -> "$hours h $minutes min"
        hours > 0L -> "$hours h"
        else -> "$minutes min"
    }
}

private fun formatDays(days: Int): String {
    return if (days == 1) "1 día" else "$days días"
}
