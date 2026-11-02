package com.example.rachapro.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.data.local.entity.ActivityEntity
import com.example.rachapro.data.local.entity.ActivityStatus
import com.example.rachapro.ui.components.RachaCard
import com.example.rachapro.ui.components.RachaSectionHeader
import com.example.rachapro.ui.components.RachaStatusBadge
import com.example.rachapro.ui.components.StatusBadgeType
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted
import com.example.rachapro.ui.theme.RachaSuccess
import com.example.rachapro.ui.theme.RachaWarning
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

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 4.dp,
            bottom = 96.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {

            RachaCard(contentPadding = 12.dp) {

                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        IconButton(
                            onClick = {
                                visibleMonth = month.minusMonths(1).toString()
                            },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Mes anterior",
                                tint = RachaIndigo
                            )
                        }

                        Text(
                            text = month.format(
                                DateTimeFormatter.ofPattern(
                                    "MMMM yyyy",
                                    SpanishLocale
                                )
                            ).replaceFirstChar {
                                it.titlecase(SpanishLocale)
                            },
                            style = MaterialTheme.typography.titleMedium
                        )

                        IconButton(
                            onClick = {
                                visibleMonth = month.plusMonths(1).toString()
                            },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Mes siguiente",
                                tint = RachaIndigo
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    CalendarGrid(
                        month = month,
                        selectedDate = selectedLocalDate,
                        activitiesByDate = activitiesByDate,
                        onDateSelected = { date ->
                            selectedDate = date.toString()
                        }
                    )
                }
            }
        }

        item {
            RachaSectionHeader(
                title = "Actividades del día",
                supportingText = selectedLocalDate.format(
                    DateTimeFormatter.ofPattern(
                        "EEEE d 'de' MMMM",
                        SpanishLocale
                    )
                ).replaceFirstChar {
                    it.titlecase(SpanishLocale)
                }
            )
        }

        if (selectedActivities.isEmpty()) {

            item {
                RachaCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🗓️", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Día libre",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "No hay actividades para esta fecha.",
                            style = MaterialTheme.typography.bodySmall,
                            color = RachaOnSurfaceMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

        } else {

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
            }.take(2)
        }

    Column {

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            weekDays.forEach { dayName ->

                Text(
                    text = dayName,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = RachaOnSurfaceMuted
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
                            .height(46.dp)
                            .padding(1.dp),
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
                                    it.status == ActivityStatus.OVERDUE
                                }

                            val allCompleted =
                                hasActivities &&
                                        dayActivities.all {
                                            it.status == ActivityStatus.COMPLETED
                                        }

                            val isSelected =
                                date == selectedDate

                            val isToday =
                                date == LocalDate.now()

                            val backgroundColor =
                                when {
                                    isSelected -> RachaIndigo
                                    isToday -> RachaIndigo.copy(alpha = 0.10f)
                                    else -> androidx.compose.ui.graphics.Color.Transparent
                                }

                            val dotColor =
                                when {
                                    allCompleted -> RachaSuccess
                                    hasOverdue -> RachaWarning
                                    else -> RachaIndigo
                                }

                            val dayLabel =
                                date.format(
                                    DateTimeFormatter.ofPattern(
                                        "EEEE d 'de' MMMM",
                                        SpanishLocale
                                    )
                                )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(backgroundColor)
                                    .clickable {
                                        onDateSelected(date)
                                    }
                                    .semantics {
                                        contentDescription = dayLabel
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
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

                                    Box(
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .size(5.dp)
                                            .background(
                                                color = if (isSelected) {
                                                    MaterialTheme.colorScheme.onPrimary
                                                } else {
                                                    dotColor
                                                },
                                                shape = CircleShape
                                            )
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

    val badgeType =
        when (activity.status) {
            ActivityStatus.COMPLETED -> StatusBadgeType.Success
            ActivityStatus.OVERDUE -> StatusBadgeType.Warning
            else -> StatusBadgeType.Pending
        }

    val statusLabel =
        when (activity.status) {
            ActivityStatus.COMPLETED -> "Completada"
            ActivityStatus.OVERDUE -> "Vencida"
            else -> "Pendiente"
        }

    RachaCard {

        Column {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                RachaStatusBadge(
                    text = statusLabel,
                    type = badgeType
                )
            }

            if (activity.description.isNotBlank()) {

                Text(
                    text = activity.description,
                    modifier = Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = RachaOnSurfaceMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val timeText = activity.dueTimeMinutes?.let { minutes ->
                String.format("%02d:%02d", minutes / 60, minutes % 60)
            }

            if (timeText != null) {

                Text(
                    text = "🕒 $timeText",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = RachaOnSurfaceMuted
                )
            }
        }
    }
}
