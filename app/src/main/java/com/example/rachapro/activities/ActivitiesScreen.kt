package com.example.rachapro.activities

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.data.local.entity.ActivityEntity
import com.example.rachapro.data.local.entity.ActivityPriority
import com.example.rachapro.data.local.entity.ActivityStatus
import com.example.rachapro.data.local.entity.CategoryEntity
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField



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
    onResetActionState: () -> Unit
) {

    LaunchedEffect(Unit) {

        onRefreshStatuses()
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

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                CircularProgressIndicator()

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Cargando actividades..."
                )
            }
        }

        is ActivitiesUiState.Success -> {

            Scaffold(
                floatingActionButton = {

                    FloatingActionButton(
                        onClick = onNewActivity
                    ) {

                        Text(
                            text = "+",
                            fontSize = 24.sp
                        )
                    }
                }
            ) { innerPadding ->

                ActivitiesSuccessContent(
                    categories = uiState.categories,

                    activities =
                        uiState.filteredActivities,

                    selectedFilter =
                        uiState.selectedFilter,

                    searchQuery =
                        uiState.searchQuery,

                    actionState =
                        actionState,

                    onFilterSelected =
                        onFilterSelected,

                    onSearchQueryChange =
                        onSearchQueryChange,

                    onEditActivity =
                        onEditActivity,

                    onCompleteActivity =
                        onCompleteActivity,

                    onDeleteActivity =
                        onDeleteActivity,

                    modifier =
                        Modifier.padding(innerPadding)
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
    selectedFilter: ActivityFilter,
    searchQuery: String,
    actionState: ActivityActionState,
    onFilterSelected: (ActivityFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onEditActivity: (Long) -> Unit,
    onCompleteActivity: (Long) -> Unit,
    onDeleteActivity: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Actividades",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Organiza tus tareas y mantén tu progreso.",
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 15.sp
            )
        }

        item {

            LazyRow(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                item {

                    FilterChip(
                        selected =
                            selectedFilter ==
                                    ActivityFilter.ALL,

                        onClick = {
                            onFilterSelected(
                                ActivityFilter.ALL
                            )
                        },

                        label = {
                            Text("Todas")
                        }
                    )
                }

                item {

                    FilterChip(
                        selected =
                            selectedFilter ==
                                    ActivityFilter.TODAY,

                        onClick = {
                            onFilterSelected(
                                ActivityFilter.TODAY
                            )
                        },

                        label = {
                            Text("Hoy")
                        }
                    )
                }

                item {

                    FilterChip(
                        selected =
                            selectedFilter ==
                                    ActivityFilter.PENDING,

                        onClick = {
                            onFilterSelected(
                                ActivityFilter.PENDING
                            )
                        },

                        label = {
                            Text("Pendientes")
                        }
                    )
                }

                item {

                    FilterChip(
                        selected =
                            selectedFilter ==
                                    ActivityFilter.OVERDUE,

                        onClick = {
                            onFilterSelected(
                                ActivityFilter.OVERDUE
                            )
                        },

                        label = {
                            Text("Vencidas")
                        }
                    )
                }

                item {

                    FilterChip(
                        selected =
                            selectedFilter ==
                                    ActivityFilter.COMPLETED,

                        onClick = {
                            onFilterSelected(
                                ActivityFilter.COMPLETED
                            )
                        },

                        label = {
                            Text("Completadas")
                        }
                    )
                }
            }
        }

        item {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Categorías",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {

            if (categories.isEmpty()) {

                Text(
                    text = "Todavía no tienes categorías."
                )

            } else {

                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    items(
                        items = categories,
                        key = { category ->
                            category.id
                        }
                    ) { category ->

                        CategoryCard(
                            category = category
                        )
                    }
                }
            }
        }

        item {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Tus actividades",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }

        when (actionState) {

            is ActivityActionState.Error -> {

                item {

                    Text(
                        text = actionState.message,
                        color = Color(0xFFFF4B55),
                        fontSize = 14.sp
                    )
                }
            }

            is ActivityActionState.ValidationError -> {

                item {

                    Text(
                        text = actionState.message,
                        color = Color(0xFFFF4B55),
                        fontSize = 14.sp
                    )
                }
            }

            else -> Unit
        }

        if (activities.isEmpty()) {

            item {

                val hasSearch =
                    searchQuery.isNotBlank()

                val title =
                    when {

                        hasSearch -> {
                            "Sin resultados"
                        }

                        selectedFilter ==
                                ActivityFilter.TODAY -> {

                            "No tienes actividades para hoy"
                        }

                        selectedFilter ==
                                ActivityFilter.PENDING -> {

                            "No tienes actividades pendientes"
                        }

                        selectedFilter ==
                                ActivityFilter.OVERDUE -> {

                            "¡Todo al día!"
                        }

                        selectedFilter ==
                                ActivityFilter.COMPLETED -> {

                            "Todavía no hay actividades completadas"
                        }

                        else -> {

                            "Todavía no tienes actividades"
                        }
                    }

                val message =
                    when {

                        hasSearch -> {

                            "No encontramos actividades que coincidan con \"$searchQuery\"."
                        }

                        selectedFilter ==
                                ActivityFilter.TODAY -> {

                            "No hay actividades programadas para hoy."
                        }

                        selectedFilter ==
                                ActivityFilter.PENDING -> {

                            "No tienes tareas pendientes en este momento."
                        }

                        selectedFilter ==
                                ActivityFilter.OVERDUE -> {

                            "No tienes actividades vencidas."
                        }

                        selectedFilter ==
                                ActivityFilter.COMPLETED -> {

                            "Cuando completes una actividad aparecerá aquí."
                        }

                        else -> {

                            "Crea tu primera actividad para empezar a organizar tus tareas."
                        }
                    }

                EmptyActivitiesCard(
                    title = title,
                    message = message
                )
            }

        } else {

            items(
                items = activities,
                key = { activity ->
                    activity.id
                }
            ) { activity ->

                val category =
                    categories.firstOrNull {
                        it.id == activity.categoryId
                    }

                ActivityCard(
                    activity = activity,
                    category = category,
                    actionState = actionState,

                    onEdit = {
                        onEditActivity(
                            activity.id
                        )
                    },

                    onComplete = {
                        onCompleteActivity(
                            activity.id
                        )
                    },

                    onDelete = {
                        onDeleteActivity(
                            activity.id
                        )
                    }
                )
            }
        }

        item {

            OutlinedTextField(
                value = searchQuery,

                onValueChange = { newQuery ->

                    onSearchQueryChange(
                        newQuery
                    )
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {

                    Text(
                        text = "Buscar actividades"
                    )
                },

                placeholder = {

                    Text(
                        text = "Título o descripción"
                    )
                },

                singleLine = true
            )
        }

        item {

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: CategoryEntity
) {

    Card {

        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = category.icon ?: "•",
                fontSize = 18.sp
            )

            Text(
                text = category.name,
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptyActivitiesCard(
    title: String,
    message: String
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )

            Text(
                text = message,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
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

    val dateFormatter =
        DateTimeFormatter.ofPattern(
            "dd/MM/yyyy"
        )

    val dueDate =
        LocalDate
            .ofEpochDay(
                activity.dueDateEpochDay
            )
            .format(
                dateFormatter
            )

    val dueTime =
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

    val isCompleted =
        activity.status ==
                ActivityStatus.COMPLETED

    val isCompleting =
        actionState is ActivityActionState.Completing &&
                actionState.activityId == activity.id

    val isDeleting =
        actionState is ActivityActionState.Deleting &&
                actionState.activityId == activity.id

    var showDeleteDialog by
    rememberSaveable(activity.id) {
        mutableStateOf(false)
    }

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {

                if (!isDeleting) {
                    showDeleteDialog = false
                }
            },

            title = {

                Text(
                    text = "¿Eliminar actividad?"
                )
            },

            text = {

                Text(
                    text =
                        "La actividad dejará de aparecer en tus listas. Esta acción no elimina físicamente el registro de la base de datos."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showDeleteDialog = false

                        onDelete()
                    },

                    enabled = !isDeleting
                ) {

                    Text(
                        text = "Eliminar"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    },

                    enabled = !isDeleting
                ) {

                    Text(
                        text = "Cancelar"
                    )
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = activity.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            if (activity.description.isNotBlank()) {

                Text(
                    text = activity.description,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    if (dueTime != null) {
                        "📅 $dueDate   🕒 $dueTime"
                    } else {
                        "📅 $dueDate"
                    },
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text =
                        "${category?.icon ?: "•"} " +
                                (category?.name ?: "Sin categoría")
                )

                Text(
                    text =
                        priorityLabel(
                            activity.priority
                        )
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedButton(
                onClick = {
                    showDeleteDialog = true
                },

                modifier = Modifier.fillMaxWidth(),

                enabled =
                    actionState !is ActivityActionState.Deleting &&
                            actionState !is ActivityActionState.Completing
            ) {

                Text(
                    text =
                        if (isDeleting) {
                            "Eliminando..."
                        } else {
                            "Eliminar"
                        }
                )
            }

            Text(
                text =
                    statusLabel(
                        activity.status
                    ),
                fontWeight = FontWeight.Medium
            )

            if (!isCompleted) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth(),
                    enabled =
                        actionState !is ActivityActionState.Completing &&
                                actionState !is ActivityActionState.Deleting &&
                                actionState !is ActivityActionState.Updating
                ) {

                    Text(
                        text = "Editar"
                    )
                }
            }

            if (!isCompleted) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    enabled =
                        actionState
                                !is ActivityActionState.Completing
                ) {

                    Text(
                        text =
                            if (isCompleting) {
                                "Completando..."
                            } else {
                                "✓ Marcar como completada"
                            }
                    )
                }
            }

        }
    }
}

private fun priorityLabel(
    priority: String
): String {

    return when (priority) {

        ActivityPriority.LOW ->
            "Prioridad baja"

        ActivityPriority.MEDIUM ->
            "Prioridad media"

        ActivityPriority.HIGH ->
            "Prioridad alta"

        else ->
            "Prioridad desconocida"
    }
}

private fun statusLabel(
    status: String
): String {

    return when (status) {

        ActivityStatus.PENDING ->
            "Pendiente"

        ActivityStatus.OVERDUE ->
            "Vencida"

        ActivityStatus.COMPLETED ->
            "Completada"

        else ->
            "Estado desconocido"
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