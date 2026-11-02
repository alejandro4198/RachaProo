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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.activities.ActivitiesScreen
import com.example.rachapro.activities.ActivitiesUiState
import com.example.rachapro.activities.ActivityActionState
import com.example.rachapro.activities.ActivityFilter
import com.example.rachapro.data.local.entity.ActivityEntity
import com.example.rachapro.data.local.entity.ActivityPriority
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
import com.example.rachapro.ui.components.RachaAvatar
import com.example.rachapro.ui.components.RachaCard
import com.example.rachapro.ui.components.RachaEmptyState
import com.example.rachapro.ui.components.RachaFadeIn
import com.example.rachapro.ui.components.RachaGradientHeader
import com.example.rachapro.ui.components.RachaLoadingScreen
import com.example.rachapro.ui.components.RachaPrimaryButton
import com.example.rachapro.ui.components.RachaProgressRing
import com.example.rachapro.ui.components.RachaSectionHeader
import com.example.rachapro.ui.components.RachaStatCard
import com.example.rachapro.ui.components.RachaStatusBadge
import com.example.rachapro.ui.components.RachaWordmark
import com.example.rachapro.ui.components.StatusBadgeType
import com.example.rachapro.ui.theme.RachaCyan
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaMint
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted
import com.example.rachapro.ui.theme.RachaStreak
import com.example.rachapro.ui.theme.RachaSuccess
import com.example.rachapro.ui.theme.RachaSurface
import com.example.rachapro.ui.theme.RachaWarning
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    onLocalActivityStatusesTick: () -> Unit,
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
                tonalElevation = 0.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = selectedTabIndex == index
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RachaIndigo,
                            selectedTextColor = RachaIndigo,
                            unselectedIconColor = RachaOnSurfaceMuted,
                            unselectedTextColor = RachaOnSurfaceMuted,
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
                        initialOffsetX = { direction * it / 8 },
                        animationSpec = tween(280)
                    )) togetherWith (fadeOut(tween(200)) + slideOutHorizontally(
                        targetOffsetX = { -direction * it / 8 },
                        animationSpec = tween(200)
                    ))
                },
                label = "mainTab"
            ) { tabIndex ->
                when (tabs[tabIndex]) {
                    MainTab.Home -> HomeContent(
                        uiState = mainUiState,
                        onRetry = onRetryUser,
                        onNewActivity = onNewActivity,
                        onOpenActivities = { selectedTabIndex = 1 },
                        onOpenPomodoro = { selectedTabIndex = 2 },
                        onOpenProgress = { selectedTabIndex = 3 }
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
                        onLocalStatusesTick = onLocalActivityStatusesTick,
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
    onRetry: () -> Unit,
    onNewActivity: () -> Unit,
    onOpenActivities: () -> Unit,
    onOpenPomodoro: () -> Unit,
    onOpenProgress: () -> Unit
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
            HomeSuccessContent(
                user = uiState,
                onNewActivity = onNewActivity,
                onOpenActivities = onOpenActivities,
                onOpenPomodoro = onOpenPomodoro,
                onOpenProgress = onOpenProgress
            )
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
    user: MainUiState.Success,
    onNewActivity: () -> Unit,
    onOpenActivities: () -> Unit,
    onOpenPomodoro: () -> Unit,
    onOpenProgress: () -> Unit
) {
    val firstName = user.fullName.trim().substringBefore(" ")
    val progress =
        if (user.totalActivitiesToday > 0) {
            user.completedActivitiesToday.toFloat() / user.totalActivitiesToday.toFloat()
        } else {
            0f
        }

    val completed = user.completedActivitiesToday
    val total = user.totalActivitiesToday
    val pending = (total - completed).coerceAtLeast(0)

    val todayLabel = LocalDate.now().format(
        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale.forLanguageTag("es"))
    ).replaceFirstChar { it.titlecase(Locale.forLanguageTag("es")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RachaWordmark()
            RachaAvatar(name = user.fullName, size = 44.dp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        RachaGradientHeader(
            title = "Hola, $firstName",
            eyebrow = todayLabel,
            subtitle = "Semestre ${user.semester} · Tu enfoque de hoy importa.",
            trailing = {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = RachaSurface
                        )
                        Text(
                            text = "hoy",
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
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RachaStatCard(
                modifier = Modifier.weight(1f),
                emoji = "🔥",
                label = "Racha actual",
                value = formatDays(user.currentStreakDays),
                accentColor = RachaStreak,
                caption = if (user.currentStreakDays > 0) "No la rompas" else "Empieza hoy"
            )
            RachaStatCard(
                modifier = Modifier.weight(1f),
                emoji = "🏆",
                label = "Mejor racha",
                value = formatDays(user.bestStreakDays),
                accentColor = RachaWarning,
                caption = "Tu récord"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        RachaCard(containerColor = MaterialTheme.colorScheme.surface) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RachaProgressRing(
                    progress = progress,
                    size = 96.dp,
                    strokeWidth = 10.dp,
                    valueText = "$completed/$total",
                    labelText = "tareas"
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Progreso de hoy",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when {
                            total == 0 -> "Aún no tienes actividades programadas para hoy."
                            pending == 0 -> "¡Todo listo! Cerraste todas tus tareas."
                            else -> "Te quedan $pending ${if (pending == 1) "actividad" else "actividades"} por completar."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = RachaOnSurfaceMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "🍅 ${user.todayCompletedPomodoros} pomodoros · ${formatFocusTimeShort(user.todayFocusSeconds)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = RachaMint
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        RachaCard(
            onClick = onOpenPomodoro,
            containerColor = RachaIndigo.copy(alpha = 0.06f),
            border = null
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(RachaIndigo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = null,
                        tint = RachaSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Entra en modo enfoque",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Un pomodoro hoy, un paso más cerca de tu meta.",
                        style = MaterialTheme.typography.bodySmall,
                        color = RachaOnSurfaceMuted
                    )
                }
                Text(
                    text = "→",
                    style = MaterialTheme.typography.titleLarge,
                    color = RachaIndigo
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        RachaSectionHeader(
            title = "Actividades de hoy",
            supportingText = if (total > 0) "$completed de $total completadas" else null,
            actionLabel = "Ver todas",
            onAction = onOpenActivities
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (user.todayActivities.isEmpty()) {
            RachaEmptyState(
                emoji = "🌤️",
                title = "Un día despejado",
                description = "No tienes actividades programadas para hoy. Crea una y empieza a sumar.",
                actionLabel = "Crear actividad",
                onAction = onNewActivity
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

        Spacer(modifier = Modifier.height(16.dp))

        RachaCard(
            onClick = onOpenProgress,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📈", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mira cómo avanzas",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Rachas, enfoque y productividad de la semana.",
                        style = MaterialTheme.typography.bodySmall,
                        color = RachaOnSurfaceMuted
                    )
                }
                Text(text = "→", color = RachaIndigo, style = MaterialTheme.typography.titleLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        RachaPrimaryButton(
            text = "Nueva actividad",
            onClick = onNewActivity,
            icon = Icons.Filled.Add
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TodayActivityCard(
    activity: ActivityEntity
) {
    val isCompleted = activity.status == ActivityStatus.COMPLETED
    val isOverdue = activity.status == ActivityStatus.OVERDUE

    val accent = when {
        isCompleted -> RachaSuccess
        isOverdue -> RachaWarning
        else -> RachaIndigo
    }

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

    RachaCard(contentPadding = 0.dp) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accent)
            )
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    RachaStatusBadge(text = badgeText, type = badgeType)
                }
                if (activity.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = RachaOnSurfaceMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (timeText != null) {
                        Text(
                            text = "🕒 $timeText",
                            style = MaterialTheme.typography.labelSmall,
                            color = RachaOnSurfaceMuted
                        )
                    }
                    val priority = priorityLabel(activity.priority)
                    if (priority != null) {
                        Text(
                            text = priority,
                            style = MaterialTheme.typography.labelSmall,
                            color = priorityColor(activity.priority)
                        )
                    }
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
        RachaCard {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "😕", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                RachaPrimaryButton(text = "Reintentar", onClick = onRetry)
            }
        }
    }
}

private fun priorityLabel(priority: String): String? = when (priority) {
    ActivityPriority.HIGH -> "Prioridad alta"
    ActivityPriority.MEDIUM -> "Prioridad media"
    ActivityPriority.LOW -> "Prioridad baja"
    else -> null
}

private fun priorityColor(priority: String): Color = when (priority) {
    ActivityPriority.HIGH -> RachaStreak
    ActivityPriority.MEDIUM -> RachaWarning
    else -> RachaOnSurfaceMuted
}

private fun formatFocusTimeShort(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    return if (minutes < 60) {
        "$minutes min"
    } else {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        if (remainingMinutes == 0L) "$hours h" else "$hours h $remainingMinutes min"
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
