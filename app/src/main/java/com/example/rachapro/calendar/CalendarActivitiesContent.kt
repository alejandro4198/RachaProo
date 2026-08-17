package com.example.rachapro.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
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
import com.example.rachapro.data.local.entity.ActivityEntity
import com.example.rachapro.data.local.entity.ActivityStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val SpanishLocale =
    Locale.forLanguageTag("es")

@Composable
fun CalendarActivitiesContent(
    activities: List<ActivityEntity>,
    modifier: Modifier = Modifier
) {

    var visibleMonth by rememberSaveable {
        mutableStateOf(
            YearMonth.now().toString()
        )
    }

    var selectedDate by rememberSaveable {
        mutableStateOf(
            LocalDate.now().toString()
        )
    }

    val month =
        YearMonth.parse(visibleMonth)

    val selectedLocalDate =
        LocalDate.parse(selectedDate)

    val activitiesByDate =
        activities.groupBy {
            it.dueDateEpochDay
        }

    val selectedActivities =
        activitiesByDate[selectedLocalDate.toEpochDay()]
            ?: emptyList()

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            FilterChip(
                selected = false,
                onClick = {
                    visibleMonth =
                        month.minusMonths(1).toString()
                },
                label = { Text("◀") }
            )

            Text(
                text = month.format(
                    DateTimeFormatter.ofPattern(
                        "MMMM yyyy",
                        SpanishLocale
                    )
                ).replaceFirstChar {
                    it.titlecase(SpanishLocale)
                },
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            FilterChip(
                selected = false,
                onClick = {
                    visibleMonth =
                        month.plusMonths(1).toString()
                },
                label = { Text("▶") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        CalendarGrid(
            month = month,
            selectedDate = selectedLocalDate,
            activitiesByDate = activitiesByDate,
            onDateSelected = { date ->
                selectedDate = date.toString()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Actividades del ${selectedLocalDate.format(
                DateTimeFormatter.ofPattern(
                    "d MMMM yyyy",
                    SpanishLocale
                )
            )}",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedActivities.isEmpty()) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {

                Text(
                    text =
                        "No hay actividades para esta fecha.",
                    modifier = Modifier.padding(16.dp)
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(
                    selectedActivities,
                    key = { it.id }
                ) { activity ->

                    CalendarActivityCard(
                        activity = activity
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    activitiesByDate: Map<Long, List<ActivityEntity>>,
    onDateSelected: (LocalDate) -> Unit
) {

    val firstDayOfMonth = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()

    val startOffset =
        (firstDayOfMonth.dayOfWeek.value - 1)

    val weekDays =
        DayOfWeek.entries.map { day ->

            day.getDisplayName(
                TextStyle.SHORT_STANDALONE,
                SpanishLocale
            ).replaceFirstChar {
                it.titlecase(SpanishLocale)
            }
        }

    Column(
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            weekDays.forEach { dayName ->

                Text(
                    text = dayName,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val totalCells =
            ((startOffset + daysInMonth + 6) / 7) * 7

        for (weekStart in 0 until totalCells step 7) {

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                for (offset in 0 until 7) {

                    val cellIndex = weekStart + offset
                    val dayNumber = cellIndex - startOffset + 1

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        if (
                            dayNumber in 1..daysInMonth
                        ) {

                            val date =
                                month.atDay(dayNumber)

                            val epochDay =
                                date.toEpochDay()

                            val dayActivities =
                                activitiesByDate[epochDay]
                                    ?: emptyList()

                            val hasActivities =
                                dayActivities.isNotEmpty()

                            val hasOverdue =
                                dayActivities.any {
                                    it.status ==
                                            ActivityStatus.OVERDUE
                                }

                            val allCompleted =
                                hasActivities &&
                                        dayActivities.all {
                                            it.status ==
                                                    ActivityStatus.COMPLETED
                                        }

                            val isSelected =
                                date == selectedDate

                            val isToday =
                                date == LocalDate.now()

                            val backgroundColor =
                                when {
                                    isSelected ->
                                        MaterialTheme.colorScheme.primary

                                    isToday ->
                                        MaterialTheme.colorScheme.primaryContainer

                                    else ->
                                        MaterialTheme.colorScheme.surface
                                }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(backgroundColor)
                                    .clickable {
                                        onDateSelected(date)
                                    },
                                horizontalAlignment =
                                    Alignment.CenterHorizontally,
                                verticalArrangement =
                                    Arrangement.Center
                            ) {

                                Text(
                                    text = dayNumber.toString(),
                                    fontWeight =
                                        if (isSelected || isToday) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        },
                                    color =
                                        if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                    fontSize = 14.sp
                                )

                                if (hasActivities) {

                                    Text(
                                        text =
                                            when {
                                                allCompleted -> "✓"
                                                hasOverdue -> "!"
                                                else -> "•"
                                            },
                                        fontSize = 10.sp,
                                        color =
                                            if (isSelected) {
                                                MaterialTheme.colorScheme.onPrimary
                                            } else {
                                                MaterialTheme.colorScheme.primary
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarActivityCard(
    activity: ActivityEntity
) {

    val statusLabel =
        when (activity.status) {

            ActivityStatus.COMPLETED ->
                "Completada"

            ActivityStatus.OVERDUE ->
                "Vencida"

            else ->
                "Pendiente"
        }

    val statusEmoji =
        when (activity.status) {

            ActivityStatus.COMPLETED -> "✅"
            ActivityStatus.OVERDUE -> "⚠️"
            else -> "⏳"
        }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "$statusEmoji ${activity.title}",
                fontWeight = FontWeight.Bold
            )

            if (activity.description.isNotBlank()) {

                Text(
                    text = activity.description,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = statusLabel,
                modifier = Modifier.padding(top = 8.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
