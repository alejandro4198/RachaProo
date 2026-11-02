package com.example.rachapro.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.calendar.CalendarActivitiesContent
import com.example.rachapro.data.local.entity.ActivityEntity
import com.example.rachapro.data.local.entity.ActivityPriority
import com.example.rachapro.data.local.entity.ActivityStatus
import com.example.rachapro.data.local.entity.CategoryEntity
import com.example.rachapro.ui.components.RachaCard
import com.example.rachapro.ui.components.RachaEmptyState
import com.example.rachapro.ui.components.RachaErrorMessage
import com.example.rachapro.ui.components.RachaGradientHeader
import com.example.rachapro.ui.components.RachaLoadingScreen
import com.example.rachapro.ui.components.RachaPrimaryButton
import com.example.rachapro.ui.components.RachaSectionHeader
import com.example.rachapro.ui.components.RachaStatusBadge
import com.example.rachapro.ui.components.StatusBadgeType
import com.example.rachapro.ui.theme.RachaError
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted
import com.example.rachapro.ui.theme.RachaStreak
import com.example.rachapro.ui.theme.RachaSuccess
import com.example.rachapro.ui.theme.RachaSurface
import com.example.rachapro.ui.theme.RachaWarning
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class ActivitiesViewMode {
    LIST,
    CALENDAR
}

@Composable
fun ActivitiesScreen(
    uiState: ActivitiesUiState,
    actionState: ActivityActionState,
    onRetry: () -> Unit,
    onNewActivity: () -> Unit,
    onEditActivity: (Long) -> Unit,
    onCompleteActivity: (Long) -> Unit,
    onDeleteActivity: (Long) -> Unit,
    onFilterSelected: (ActivityFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onRefreshStatuses: () -> Unit,
    onLocalStatusesTick: () -> Unit,
    onResetActionState: () -> Unit
) {

    LaunchedEffect(Unit) {
        onRefreshStatuses()
        while (true) {
            val millisUntilNextMinute = 60_000L - (System.currentTimeMillis() % 60_000L)
            delay(millisUntilNextMinute + 250L)
            onLocalStatusesTick()
        }
    }

    LaunchedEffect(actionState) {
        if (
            actionState is ActivityActionState.DeleteSuccess ||
            actionState is ActivityActionState.CompleteSuccess
        ) {
            onResetActionState()
        }
    }

    when (uiState) {
        ActivitiesUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                RachaLoadingScreen("Cargando actividades...")
            }
        }

        is ActivitiesUiState.Success -> {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = onNewActivity,
                        containerColor = RachaIndigo,
                        contentColor = RachaSurface,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(
                                text = "Nueva",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            ) { innerPadding ->
                ActivitiesSuccessContent(
                    categories = uiState.categories,
                    activities = uiState.activities,
                    filteredActivities = uiState.filteredActivities,
                    selectedFilter = uiState.selectedFilter,
                    searchQuery = uiState.searchQuery,
                    actionState = actionState,
                    onFilterSelected = onFilterSelected,
                    onSearchQueryChange = onSearchQueryChange,
                    onEditActivity = onEditActivity,
                    onCompleteActivity = onCompleteActivity,
                    onDeleteActivity = onDeleteActivity,
                    onNewActivity = onNewActivity,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        ActivitiesUiState.NoActiveSession -> {
            ActivitiesErrorContent(
                message = "No hay una sesión activa.",
                onRetry = onRetry
            )
        }

        ActivitiesUiState.Error -> {
            ActivitiesErrorContent(
                message = "No fue posible cargar las actividades.",
                onRetry = onRetry
            )
        }
    }
}

@Composable
private fun ActivitiesSuccessContent(
    categories: List<CategoryEntity>,
    activities: List<ActivityEntity>,
    filteredActivities: List<ActivityEntity>,
    selectedFilter: ActivityFilter,
    searchQuery: String,
    actionState: ActivityActionState,
    onFilterSelected: (ActivityFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onEditActivity: (Long) -> Unit,
    onCompleteActivity: (Long) -> Unit,
    onDeleteActivity: (Long) -> Unit,
    onNewActivity: () -> Unit,
    modifier: Modifier = Modifier
) {

    var viewMode by rememberSaveable {
        mutableStateOf(ActivitiesViewMode.LIST)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = viewMode == ActivitiesViewMode.LIST,
                onClick = { viewMode = ActivitiesViewMode.LIST },
                label = { Text("Lista") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = RachaIndigo
                    )
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RachaIndigo.copy(alpha = 0.12f),
                    selectedLabelColor = RachaIndigo
                )
            )

            FilterChip(
                selected = viewMode == ActivitiesViewMode.CALENDAR,
                onClick = { viewMode = ActivitiesViewMode.CALENDAR },
                label = { Text("Calendario") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = RachaIndigo
                    )
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RachaIndigo.copy(alpha = 0.12f),
                    selectedLabelColor = RachaIndigo
                )
            )
        }

        when (viewMode) {
            ActivitiesViewMode.LIST -> {
                ActivitiesListContent(
                    categories = categories,
                    activities = filteredActivities,
                    selectedFilter = selectedFilter,
                    searchQuery = searchQuery,
                    actionState = actionState,
                    onFilterSelected = onFilterSelected,
                    onSearchQueryChange = onSearchQueryChange,
                    onEditActivity = onEditActivity,
                    onCompleteActivity = onCompleteActivity,
                    onDeleteActivity = onDeleteActivity,
                    onNewActivity = onNewActivity
                )
            }

            ActivitiesViewMode.CALENDAR -> {
                CalendarActivitiesContent(
                    activities = activities,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun ActivitiesListContent(
    categories: List<CategoryEntity>,
    activities: List<ActivityEntity>,
    selectedFilter: ActivityFilter,
    searchQuery: String,
    actionState: ActivityActionState,
    onFilterSelected: (ActivityFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onEditActivity: (Long) -> Unit,
    onCompleteActivity: (Long) -> Unit,
    onDeleteActivity: (Long) -> Unit,
    onNewActivity: () -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            RachaGradientHeader(
                title = "Actividades",
                eyebrow = "Tu organización",
                subtitle = "Ordena tus tareas y protege tu racha.",
                trailing = {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(RachaSurface.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = activities.size.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = RachaSurface
                            )
                            Text(
                                text = "tareas",
                                style = MaterialTheme.typography.labelSmall,
                                color = RachaSurface.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            )
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Buscar por título o descripción",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = RachaOnSurfaceMuted
                    )
                },
                singleLine = true,
                shape = MaterialTheme.shapes.small
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    ActivityFilterChip(
                        label = "Todas",
                        selected = selectedFilter == ActivityFilter.ALL,
                        onClick = { onFilterSelected(ActivityFilter.ALL) }
                    )
                }
                item {
                    ActivityFilterChip(
                        label = "Hoy",
                        selected = selectedFilter == ActivityFilter.TODAY,
                        onClick = { onFilterSelected(ActivityFilter.TODAY) }
                    )
                }
                item {
                    ActivityFilterChip(
                        label = "Pendientes",
                        selected = selectedFilter == ActivityFilter.PENDING,
                        onClick = { onFilterSelected(ActivityFilter.PENDING) }
                    )
                }
                item {
                    ActivityFilterChip(
                        label = "Vencidas",
                        selected = selectedFilter == ActivityFilter.OVERDUE,
                        onClick = { onFilterSelected(ActivityFilter.OVERDUE) }
                    )
                }
                item {
                    ActivityFilterChip(
                        label = "Completadas",
                        selected = selectedFilter == ActivityFilter.COMPLETED,
                        onClick = { onFilterSelected(ActivityFilter.COMPLETED) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(2.dp))
            RachaSectionHeader(
                title = "Categorías",
                supportingText = if (categories.isEmpty()) "Crea categorías desde tus actividades." else "Filtra visualmente tu semestre"
            )
        }

        item {
            if (categories.isEmpty()) {
                Text(
                    text = "Todavía no tienes categorías.",
                    style = MaterialTheme.typography.bodySmall,
                    color = RachaOnSurfaceMuted
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items = categories, key = { it.id }) { category ->
                        CategoryChip(
                            category = category,
                            onClick = {
                                // Category filter - update UI state
                                // TODO: integrate with activitiesUiState.selectedCategory if needed
                            }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            RachaSectionHeader(
                title = "Tus actividades",
                supportingText = when {
                    searchQuery.isNotBlank() -> "Resultados para \"$searchQuery\""
                    activities.isEmpty() -> null
                    else -> "${activities.size} ${if (activities.size == 1) "actividad" else "actividades"}"
                }
            )
        }

        when (actionState) {
            is ActivityActionState.Error -> {
                item { RachaErrorMessage(message = actionState.message) }
            }

            is ActivityActionState.ValidationError -> {
                item { RachaErrorMessage(message = actionState.message) }
            }

            else -> Unit
        }

        if (activities.isEmpty()) {
            item {
                val hasSearch = searchQuery.isNotBlank()

                val title = when {
                    hasSearch -> "Sin resultados"
                    selectedFilter == ActivityFilter.TODAY -> "No tienes actividades para hoy"
                    selectedFilter == ActivityFilter.PENDING -> "No tienes actividades pendientes"
                    selectedFilter == ActivityFilter.OVERDUE -> "¡Todo al día!"
                    selectedFilter == ActivityFilter.COMPLETED -> "Todavía no hay actividades completadas"
                    else -> "Todavía no tienes actividades"
                }

                val message = when {
                    hasSearch -> "No encontramos actividades que coincidan con \"$searchQuery\"."
                    selectedFilter == ActivityFilter.TODAY -> "No hay actividades programadas para hoy."
                    selectedFilter == ActivityFilter.PENDING -> "No tienes tareas pendientes en este momento."
                    selectedFilter == ActivityFilter.OVERDUE -> "No tienes actividades vencidas."
                    selectedFilter == ActivityFilter.COMPLETED -> "Cuando completes una actividad aparecerá aquí."
                    else -> "Crea tu primera actividad para empezar a organizar tus tareas."
                }

                val emoji = when {
                    hasSearch -> "🔍"
                    selectedFilter == ActivityFilter.OVERDUE -> "🎉"
                    selectedFilter == ActivityFilter.COMPLETED -> "🏁"
                    selectedFilter == ActivityFilter.PENDING -> "✅"
                    selectedFilter == ActivityFilter.TODAY -> "📅"
                    else -> "📝"
                }

                RachaEmptyState(
                    emoji = emoji,
                    title = title,
                    description = message,
                    actionLabel = if (hasSearch) null else "Crear actividad",
                    onAction = if (hasSearch) null else onNewActivity
                )
            }
        } else {
            items(items = activities, key = { it.id }) { activity ->
                val category = categories.firstOrNull { it.id == activity.categoryId }

                ActivityCard(
                    activity = activity,
                    category = category,
                    actionState = actionState,
                    onEdit = { onEditActivity(activity.id) },
                    onComplete = { onCompleteActivity(activity.id) },
                    onDelete = { onDeleteActivity(activity.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

@Composable
private fun ActivityFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = RachaIndigo.copy(alpha = 0.12f),
            selectedLabelColor = RachaIndigo
        )
    )
}

@Composable
private fun CategoryChip(
    category: CategoryEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = category.icon ?: "•",
            fontSize = 16.sp
        )
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1
        )
    }
}

@Composable
private fun ActivityCard(
    activity: ActivityEntity,
    category: CategoryEntity?,
    actionState: ActivityActionState,
    onEdit: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
) {

    val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("es"))
    val dueDate = LocalDate.ofEpochDay(activity.dueDateEpochDay).format(dateFormatter)
    val dueTime = activity.dueTimeMinutes?.let { minutes ->
        String.format("%02d:%02d", minutes / 60, minutes % 60)
    }

    val isCompleted = activity.status == ActivityStatus.COMPLETED
    val isOverdue = activity.status == ActivityStatus.OVERDUE
    val isCompleting = actionState is ActivityActionState.Completing && actionState.activityId == activity.id
    val isDeleting = actionState is ActivityActionState.Deleting && actionState.activityId == activity.id

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

    var showDeleteDialog by rememberSaveable(activity.id) { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) showDeleteDialog = false
            },
            title = { Text(text = "¿Eliminar actividad?") },
            text = {
                Text(text = "\"${activity.title}\" se quitará de tus listas y no podrás recuperarla desde la app.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    enabled = !isDeleting
                ) {
                    Text(text = if (isDeleting) "Eliminando..." else "Eliminar", color = RachaError)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    enabled = !isDeleting
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }

    val isBusy =
        actionState is ActivityActionState.Completing ||
            actionState is ActivityActionState.Deleting ||
            actionState is ActivityActionState.Updating

    RachaCard(contentPadding = 0.dp) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accent)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RachaStatusBadge(
                        text = statusLabel(activity.status),
                        type = badgeType
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (category != null) {
                        Text(
                            text = "${category.icon ?: "•"} ${category.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = RachaOnSurfaceMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

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

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅 $dueDate${if (dueTime != null) " · $dueTime" else ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = RachaOnSurfaceMuted
                    )
                    PriorityIndicator(priority = activity.priority)
                }

                if (!isCompleted) {
                    Spacer(modifier = Modifier.height(14.dp))
                    RachaPrimaryButton(
                        text = if (isCompleting) "Completando..." else "Marcar como completada",
                        onClick = onComplete,
                        enabled = !isBusy,
                        icon = Icons.Filled.Check
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isCompleted) {
                        TextButton(
                            onClick = onEdit,
                            enabled = !isBusy
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Editar")
                        }
                    }
                    TextButton(
                        onClick = { showDeleteDialog = true },
                        enabled = !isBusy
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = RachaError
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isDeleting) "Eliminando..." else "Eliminar",
                            color = RachaError
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityIndicator(
    priority: String
) {
    val label = priorityLabel(priority)
    val color = when (priority) {
        ActivityPriority.HIGH -> RachaStreak
        ActivityPriority.MEDIUM -> RachaWarning
        ActivityPriority.LOW -> RachaOnSurfaceMuted
        else -> RachaOnSurfaceMuted
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

private fun priorityLabel(priority: String): String {
    return when (priority) {
        ActivityPriority.LOW -> "Prioridad baja"
        ActivityPriority.MEDIUM -> "Prioridad media"
        ActivityPriority.HIGH -> "Prioridad alta"
        else -> "Prioridad desconocida"
    }
}

private fun statusLabel(status: String): String {
    return when (status) {
        ActivityStatus.PENDING -> "Pendiente"
        ActivityStatus.OVERDUE -> "Vencida"
        ActivityStatus.COMPLETED -> "Completada"
        else -> "Estado desconocido"
    }
}

@Composable
private fun ActivitiesErrorContent(
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
                Text(text = "📚", fontSize = 32.sp)
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
