package com.example.rachapro.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.activities.ActivitiesScreen
import com.example.rachapro.activities.ActivitiesUiState
import com.example.rachapro.activities.ActivityActionState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.rachapro.data.local.entity.ActivityEntity
import com.example.rachapro.data.local.entity.ActivityStatus
import com.example.rachapro.activities.ActivityFilter
import com.example.rachapro.pomodoro.PomodoroActionState
import com.example.rachapro.pomodoro.PomodoroScreen
import com.example.rachapro.pomodoro.PomodoroUiState
import com.example.rachapro.progress.ProgressScreen
import com.example.rachapro.progress.ProgressUiState
import com.example.rachapro.progress.ProgressPeriod



@Composable
fun MainScreen(
    mainUiState: MainUiState,
    activitiesUiState: ActivitiesUiState,
    activityActionState: ActivityActionState,
    isLoggingOut: Boolean,
    onRetryUser: () -> Unit,
    onRetryActivities: () -> Unit,
    onNewActivity: () -> Unit,
    onEditActivity: (Long) -> Unit,
    onCompleteActivity: (Long) -> Unit,
    onDeleteActivity: (Long) -> Unit,
    onResetActivityActionState: () -> Unit,
    onLogout: () -> Unit,
    onActivityFilterSelected: (ActivityFilter) -> Unit,
    onActivitySearchQueryChange: (String) -> Unit,
    onRefreshActivityStatuses: () -> Unit,
    pomodoroUiState: PomodoroUiState,
    pomodoroActionState: PomodoroActionState,
    onStartPomodoro: () -> Unit,
    onStartShortBreak: () -> Unit,
    onStartLongBreak: () -> Unit,
    onPausePomodoro: () -> Unit,
    onResumePomodoro: () -> Unit,
    onCancelPomodoro: () -> Unit,
    progressUiState: ProgressUiState,
    onRetryProgress: () -> Unit,
    onProgressPeriodSelected: (ProgressPeriod) -> Unit,
) {

    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val tabs = listOf(
        MainTab.Home,
        MainTab.Activities,
        MainTab.Pomodoro,
        MainTab.Progress,
        MainTab.Profile
    )

    Scaffold(
        bottomBar = {

            NavigationBar {

                tabs.forEachIndexed { index, tab ->

                    NavigationBarItem(
                        selected =
                            selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                        },
                        icon = {
                            Text(
                                text = tab.symbol
                            )
                        },
                        label = {
                            Text(
                                text = tab.label
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (
                tabs[selectedTabIndex]
            ) {

                MainTab.Home -> {

                    HomeContent(
                        uiState = mainUiState,
                        onRetry = onRetryUser
                    )
                }

                MainTab.Activities -> {

                    ActivitiesScreen(
                        uiState = activitiesUiState,
                        actionState = activityActionState,
                        onRetry = onRetryActivities,
                        onNewActivity = onNewActivity,
                        onEditActivity = onEditActivity,
                        onCompleteActivity = onCompleteActivity,
                        onDeleteActivity = onDeleteActivity,
                        onResetActionState = onResetActivityActionState,
                        onFilterSelected = onActivityFilterSelected,
                        onSearchQueryChange = onActivitySearchQueryChange,
                        onRefreshStatuses = onRefreshActivityStatuses,
                    )
                }

                MainTab.Pomodoro -> {

                    PomodoroScreen(
                        uiState = pomodoroUiState,
                        actionState = pomodoroActionState,
                        onStartFocus = onStartPomodoro,
                        onStartShortBreak = onStartShortBreak,
                        onStartLongBreak = onStartLongBreak,
                        onPause = onPausePomodoro,
                        onResume = onResumePomodoro,
                        onCancel = onCancelPomodoro
                    )
                }

                MainTab.Progress -> {

                    ProgressScreen(
                        uiState = progressUiState,
                        onRetry = onRetryProgress,
                        onPeriodSelected =
                            onProgressPeriodSelected
                    )
                }

                MainTab.Profile -> {

                    ProfilePlaceholderScreen(
                        mainUiState = mainUiState,
                        isLoggingOut = isLoggingOut,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    uiState: MainUiState,
    onRetry: () -> Unit
) {

    when (uiState) {

        MainUiState.Loading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()
            }
        }

        is MainUiState.Success -> {

            HomeSuccessContent(
                user = uiState
            )
        }

        MainUiState.NoActiveSession -> {

            MainErrorContent(
                message =
                    "No hay una sesión activa.",
                onRetry = onRetry
            )
        }

        MainUiState.UserNotFound -> {

            MainErrorContent(
                message =
                    "No fue posible encontrar el usuario.",
                onRetry = onRetry
            )
        }

        MainUiState.Error -> {

            MainErrorContent(
                message =
                    "Ocurrió un error al cargar la información.",
                onRetry = onRetry
            )
        }
    }
}

@Composable
private fun HomeSuccessContent(
    user: MainUiState.Success
) {

    val firstName =
        user.fullName
            .trim()
            .substringBefore(" ")

    val progress =
        if (user.totalActivitiesToday > 0) {

            user.completedActivitiesToday.toFloat() /
                    user.totalActivitiesToday.toFloat()

        } else {

            0f
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ) {

        Text(
            text = "Hola, $firstName 👋",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Semestre ${user.semester}",
            modifier = Modifier.padding(top = 4.dp),
            fontSize = 15.sp
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        /*
         * Las rachas siguen en cero porque
         * todavía no hemos implementado
         * su cálculo real.
         */

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            StatCard(
                modifier = Modifier.weight(1f),
                title = "🔥 Racha actual",
                value = formatDays(
                    user.currentStreakDays
                )
            )

            StatCard(
                modifier = Modifier.weight(1f),
                title = "🏆 Mejor racha",
                value = formatDays(
                    user.bestStreakDays
                )
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        /*
         * -----------------------------------------------------
         * PROGRESO REAL DE HOY
         * -----------------------------------------------------
         */

        Text(
            text = "Progreso de hoy",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LinearProgressIndicator(
            progress = {
                progress
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "${user.completedActivitiesToday} " +
                        "de ${user.totalActivitiesToday} " +
                        "actividades completadas"
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        /*
         * -----------------------------------------------------
         * ACTIVIDADES REALES DE HOY
         * -----------------------------------------------------
         */

        Text(
            text = "Actividades de hoy",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (user.todayActivities.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text =
                            "No tienes actividades programadas para hoy",
                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Text(
                        text =
                            "Las actividades que programes para hoy aparecerán aquí.",
                        modifier =
                            Modifier.padding(top = 6.dp)
                    )
                }
            }

        } else {

            user.todayActivities.forEach { activity ->

                TodayActivityCard(
                    activity = activity
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

@Composable
private fun TodayActivityCard(
    activity: ActivityEntity
) {

    val isCompleted =
        activity.status ==
                ActivityStatus.COMPLETED

    val timeText =
        activity.dueTimeMinutes
            ?.let { minutes ->

                val hour =
                    minutes / 60

                val minute =
                    minutes % 60

                String.format(
                    "%02d:%02d",
                    hour,
                    minute
                )
            }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text =
                    if (isCompleted) {
                        "✅ ${activity.title}"
                    } else {
                        "⏳ ${activity.title}"
                    },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            if (activity.description.isNotBlank()) {

                Text(
                    text = activity.description,
                    modifier =
                        Modifier.padding(top = 4.dp),
                    fontSize = 14.sp
                )
            }

            if (timeText != null) {

                Text(
                    text = "🕒 $timeText",
                    modifier =
                        Modifier.padding(top = 8.dp),
                    fontSize = 14.sp
                )
            }

            Text(
                text =
                    if (isCompleted) {
                        "Completada"
                    } else {
                        "Pendiente"
                    },
                modifier =
                    Modifier.padding(top = 8.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {

    Card(
        modifier = modifier
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
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
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MainErrorContent(
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
private fun PlaceholderScreen(
    title: String
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfilePlaceholderScreen(
    mainUiState: MainUiState,
    isLoggingOut: Boolean,
    onLogout: () -> Unit
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
            text = "Perfil",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        if (mainUiState is MainUiState.Success) {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = mainUiState.fullName,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = mainUiState.email
            )

            Text(
                text =
                    "Semestre ${mainUiState.semester}"
            )
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Button(
            onClick = onLogout,
            enabled = !isLoggingOut
        ) {

            Text(
                text =
                    if (isLoggingOut) {
                        "Cerrando sesión..."
                    } else {
                        "Cerrar sesión"
                    }
            )
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

private sealed class MainTab(
    val label: String,
    val symbol: String
) {

    data object Home :
        MainTab(
            label = "Inicio",
            symbol = "⌂"
        )

    data object Activities :
        MainTab(
            label = "Actividades",
            symbol = "✓"
        )

    data object Pomodoro :
        MainTab(
            label = "Pomodoro",
            symbol = "◷"
        )

    data object Progress :
        MainTab(
            label = "Progreso",
            symbol = "↗"
        )

    data object Profile :
        MainTab(
            label = "Perfil",
            symbol = "●"
        )
}