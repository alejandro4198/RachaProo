package com.example.rachapro.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rachapro.activities.ActivitiesScreen
import com.example.rachapro.activities.ActivitiesUiState
import com.example.rachapro.activities.ActivityActionState
import com.example.rachapro.activities.ActivityFilter
import com.example.rachapro.data.local.entity.ActivityEntity
import com.example.rachapro.data.local.entity.ActivityStatus
import com.example.rachapro.pomodoro.PomodoroActionState
import com.example.rachapro.pomodoro.PomodoroScreen
import com.example.rachapro.pomodoro.PomodoroUiState
import com.example.rachapro.profile.ProfileSaveState
import com.example.rachapro.profile.ProfileScreen
import com.example.rachapro.profile.ProfileUiState
import com.example.rachapro.progress.ProgressPeriod
import com.example.rachapro.progress.ProgressScreen
import com.example.rachapro.progress.ProgressUiState
import com.example.rachapro.ui.components.RachaAnimatedProgress
import com.example.rachapro.ui.components.RachaCard
import com.example.rachapro.ui.components.RachaEmptyState
import com.example.rachapro.ui.components.RachaFadeIn
import com.example.rachapro.ui.components.RachaGradientHeader
import com.example.rachapro.ui.components.RachaLoadingScreen
import com.example.rachapro.ui.components.RachaPrimaryButton
import com.example.rachapro.ui.components.RachaStatCard
import com.example.rachapro.ui.components.RachaStatusBadge
import com.example.rachapro.ui.components.StatusBadgeType
import com.example.rachapro.ui.components.rememberStreakAccent
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaStreak
import com.example.rachapro.ui.theme.RachaSuccess

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
    profileUiState: ProfileUiState,
    profileSaveState: ProfileSaveState,
    notificationsPermissionGranted: Boolean,
    onRetryProfile: () -> Unit,
    onPomodoroDraftChange: (
        focusMinutes: Int,
        shortBreakMinutes: Int,
        longBreakMinutes: Int
    ) -> Unit,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    onSaveProfilePreferences: () -> Unit,
    onProfileDraftChange: (String, Int) -> Unit,
    onSaveProfile: () -> Unit,
    onDismissPomodoroCompleted: () -> Unit,
    onResetProfileSaveState: () -> Unit,
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
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = selectedTabIndex == index
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RachaIndigo,
                            selectedTextColor = RachaIndigo,
                            indicatorColor = RachaIndigo.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (fadeIn(tween(280)) + slideInHorizontally(
                        initialOffsetX = { direction * it / 6 },
                        animationSpec = tween(280)
                    )) togetherWith (fadeOut(tween(200)) + slideOutHorizontally(
                        targetOffsetX = { -direction * it / 6 },
                        animationSpec = tween(200)
                    ))
                },
                label = "mainTab"
            ) { tabIndex ->
                when (tabs[tabIndex]) {
                    MainTab.Home -> HomeContent(
                        uiState = mainUiState,
                        onRetry = onRetryUser
                    )

                    MainTab.Activities -> ActivitiesScreen(
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

                    MainTab.Pomodoro -> PomodoroScreen(
                        uiState = pomodoroUiState,
                        actionState = pomodoroActionState,
                        onStartFocus = onStartPomodoro,
                        onStartShortBreak = onStartShortBreak,
                        onStartLongBreak = onStartLongBreak,
                        onPause = onPausePomodoro,
                        onResume = onResumePomodoro,
                        onCancel = onCancelPomodoro,
                        onDismissCompleted = onDismissPomodoroCompleted
                    )

                    MainTab.Progress -> ProgressScreen(
                        uiState = progressUiState,
                        onRetry = onRetryProgress,
                        onPeriodSelected = onProgressPeriodSelected
                    )

                    MainTab.Profile -> ProfileScreen(
                        uiState = profileUiState,
                        saveState = profileSaveState,
                        isLoggingOut = isLoggingOut,
                        notificationsPermissionGranted = notificationsPermissionGranted,
                        onRetry = onRetryProfile,
                        onPomodoroDraftChange = onPomodoroDraftChange,
                        onNotificationsEnabledChange = onNotificationsEnabledChange,
                        onSavePreferences = onSaveProfilePreferences,
                        onProfileDraftChange = onProfileDraftChange,
                        onSaveProfile = onSaveProfile,
                        onResetSaveState = onResetProfileSaveState,
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
                RachaLoadingScreen("Preparando tu día...")
            }
        }

        is MainUiState.Success -> {
            HomeSuccessContent(user = uiState)
        }

        MainUiState.NoActiveSession -> {
            MainErrorContent(
                message = "No hay una sesión activa.",
                onRetry = onRetry
            )
        }

        MainUiState.UserNotFound -> {
            MainErrorContent(
                message = "No fue posible encontrar el usuario.",
                onRetry = onRetry
            )
        }

        MainUiState.Error -> {
            MainErrorContent(
                message = "Ocurrió un error al cargar la información.",
                onRetry = onRetry
            )
        }
    }
}

@Composable
private fun HomeSuccessContent(
    user: MainUiState.Success
) {
    val firstName = user.fullName.trim().substringBefore(" ")
    val progress =
        if (user.totalActivitiesToday > 0) {
            user.completedActivitiesToday.toFloat() / user.totalActivitiesToday.toFloat()
        } else {
            0f
        }

    val streakAccent = rememberStreakAccent(user.currentStreakDays)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        RachaGradientHeader(
            title = "Hola, $firstName 👋",
            subtitle = "Semestre ${user.semester} · Mantén tu racha hoy"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RachaStatCard(
                modifier = Modifier.weight(1f),
                emoji = "🔥",
                label = "Racha actual",
                value = formatDays(user.currentStreakDays),
                accentColor = streakAccent
            )
            RachaStatCard(
                modifier = Modifier.weight(1f),
                emoji = "🏆",
                label = "Mejor racha",
                value = formatDays(user.bestStreakDays),
                accentColor = RachaStreak
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Progreso de hoy",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        RachaCard {
            RachaAnimatedProgress(
                progress = progress,
                label = "${user.completedActivitiesToday} de ${user.totalActivitiesToday} actividades"
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "🍅 ${user.todayCompletedPomodoros} Pomodoros · ${formatFocusTime(user.todayFocusSeconds)}",
                style = MaterialTheme.typography.bodyMedium,
                color = RachaOnSurfaceMuted
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Actividades de hoy",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (user.todayActivities.isEmpty()) {
            RachaEmptyState(
                emoji = "📋",
                title = "Sin actividades hoy",
                description = "Las tareas que programes para hoy aparecerán aquí."
            )
        } else {
            user.todayActivities.forEachIndexed { index, activity ->
                RachaFadeIn(visible = true) {
                    TodayActivityCard(activity = activity)
                }
                if (index < user.todayActivities.lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TodayActivityCard(
    activity: ActivityEntity
) {
    val isCompleted = activity.status == ActivityStatus.COMPLETED
    val isOverdue = activity.status == ActivityStatus.OVERDUE

    val badgeType = when {
        isCompleted -> StatusBadgeType.Success
        isOverdue -> StatusBadgeType.Warning
        else -> StatusBadgeType.Pending
    }

    val badgeText = when {
        isCompleted -> "Completada"
        isOverdue -> "Vencida"
        else -> "Pendiente"
    }

    val timeText = activity.dueTimeMinutes?.let { minutes ->
        String.format("%02d:%02d", minutes / 60, minutes % 60)
    }

    RachaCard {
        Column {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (activity.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = RachaOnSurfaceMuted
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RachaStatusBadge(text = badgeText, type = badgeType)
                if (timeText != null) {
                    Text(
                        text = "🕒 $timeText",
                        style = MaterialTheme.typography.labelMedium,
                        color = RachaOnSurfaceMuted
                    )
                }
            }
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        RachaPrimaryButton(text = "Reintentar", onClick = onRetry)
    }
}

private fun formatFocusTime(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    return if (minutes < 60) {
        "$minutes min de enfoque"
    } else {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        "$hours h $remainingMinutes min de enfoque"
    }
}

private fun formatDays(days: Int): String {
    return if (days == 1) "1 día" else "$days días"
}

private sealed class MainTab(
    val label: String,
    val icon: ImageVector
) {
    data object Home : MainTab("Inicio", Icons.Filled.Home)
    data object Activities : MainTab("Actividades", Icons.Outlined.TaskAlt)
    data object Pomodoro : MainTab("Pomodoro", Icons.Filled.Timer)
    data object Progress : MainTab("Progreso", Icons.Filled.TrendingUp)
    data object Profile : MainTab("Perfil", Icons.Filled.Person)
}
