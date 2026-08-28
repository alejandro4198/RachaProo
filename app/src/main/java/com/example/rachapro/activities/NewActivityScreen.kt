package com.example.rachapro.activities

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.data.local.entity.ActivityEntity
import com.example.rachapro.data.local.entity.ActivityPriority
import com.example.rachapro.data.local.entity.CategoryEntity
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import androidx.compose.material3.Checkbox
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.rachapro.notifications.NotificationPermission
import java.time.ZoneId

@Composable
fun NewActivityScreen(
    categories: List<CategoryEntity>,
    actionState: ActivityActionState,
    onBack: () -> Unit,
    onCreateActivity: (
        title: String,
        description: String,
        categoryId: Long?,
        dueDateEpochDay: Long?,
        dueTimeMinutes: Int?,
        priority: String
    ) -> Unit,
    onCreated: () -> Unit,
    onResetActionState: () -> Unit
) {

    ActivityFormScreen(
        activity = null,

        categories = categories,

        actionState = actionState,

        screenTitle =
            "Nueva actividad",

        screenDescription =
            "Agrega la información de tu actividad.",

        buttonText =
            "Crear actividad",

        loadingButtonText =
            "Creando actividad...",

        onBack =
            onBack,

        onSubmit = {
                title,
                description,
                categoryId,
                dueDateEpochDay,
                dueTimeMinutes,
                priority ->

            onCreateActivity(
                title,
                description,
                categoryId,
                dueDateEpochDay,
                dueTimeMinutes,
                priority
            )
        },

        onSuccess =
            onCreated,

        onResetActionState =
            onResetActionState
    )
}

@Composable
fun EditActivityScreen(
    activity: ActivityEntity,
    categories: List<CategoryEntity>,
    actionState: ActivityActionState,
    subtasksUiState: SubtasksUiState,
    subtaskActionState: SubtaskActionState,
    reminderUiState: ReminderUiState,
    reminderActionState: ReminderActionState,
    onBack: () -> Unit,
    onUpdateActivity: (
        activityId: Long,
        title: String,
        description: String,
        categoryId: Long?,
        dueDateEpochDay: Long?,
        dueTimeMinutes: Int?,
        priority: String
    ) -> Unit,
    onCreateSubtask: (String) -> Unit,
    onSetSubtaskCompleted: (
        subtaskId: Long,
        isCompleted: Boolean
    ) -> Unit,
    onUpdateSubtask: (
        subtaskId: Long,
        title: String
    ) -> Unit,
    onDeleteSubtask: (
        subtaskId: Long
    ) -> Unit,
    onUpdated: () -> Unit,
    onResetActionState: () -> Unit,
    onResetSubtaskActionState: () -> Unit,
    onCreateReminder: (
        title: String,
        message: String,
        triggerAtMillis: Long
    ) -> Unit,
    onCancelReminder: (
        reminderId: Long
    ) -> Unit,
) {

    ActivityFormScreen(
        activity =
            activity,

        categories =
            categories,

        actionState =
            actionState,

        screenTitle =
            "Editar actividad",

        screenDescription =
            "Modifica la información de tu actividad.",

        buttonText =
            "Guardar cambios",

        loadingButtonText =
            "Guardando cambios...",

        onBack =
            onBack,

        onSubmit = {
                title,
                description,
                categoryId,
                dueDateEpochDay,
                dueTimeMinutes,
                priority ->

            onUpdateActivity(
                activity.id,
                title,
                description,
                categoryId,
                dueDateEpochDay,
                dueTimeMinutes,
                priority
            )
        },

        onSuccess =
            onUpdated,

        onResetActionState =
            onResetActionState,

        extraContent = {

            SubtasksSection(
                uiState =
                    subtasksUiState,

                actionState =
                    subtaskActionState,

                onCreateSubtask =
                    onCreateSubtask,

                onSetSubtaskCompleted =
                    onSetSubtaskCompleted,

                onUpdateSubtask =
                    onUpdateSubtask,

                onDeleteSubtask =
                    onDeleteSubtask,

                onResetActionState =
                    onResetSubtaskActionState
            )

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            RemindersSection(
                uiState =
                    reminderUiState,

                actionState =
                    reminderActionState,

                defaultTitle =
                    "Recordatorio: ${activity.title}",

                onCreateReminder =
                    onCreateReminder,

                onCancelReminder =
                    onCancelReminder
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityFormScreen(
    activity: ActivityEntity?,
    categories: List<CategoryEntity>,
    actionState: ActivityActionState,
    screenTitle: String,
    screenDescription: String,
    buttonText: String,
    loadingButtonText: String,
    onBack: () -> Unit,
    onSubmit: (
        title: String,
        description: String,
        categoryId: Long?,
        dueDateEpochDay: Long?,
        dueTimeMinutes: Int?,
        priority: String
    ) -> Unit,
    onSuccess: () -> Unit,
    onResetActionState: () -> Unit,
    extraContent: (@Composable () -> Unit)? = null
) {

    val isEditing =
        activity != null

    var title by
    rememberSaveable(
        activity?.id
    ) {

        mutableStateOf(
            activity
                ?.title
                .orEmpty()
        )
    }

    var description by
    rememberSaveable(
        activity?.id
    ) {

        mutableStateOf(
            activity
                ?.description
                .orEmpty()
        )
    }

    var selectedCategoryId by
    rememberSaveable(
        activity?.id
    ) {

        mutableStateOf(
            activity?.categoryId
        )
    }

    var selectedDateEpochDay by
    rememberSaveable(
        activity?.id
    ) {

        mutableStateOf(
            activity?.dueDateEpochDay
        )
    }

    var selectedTimeMinutes by
    rememberSaveable(
        activity?.id
    ) {

        mutableStateOf(
            activity?.dueTimeMinutes
        )
    }

    var selectedPriority by
    rememberSaveable(
        activity?.id
    ) {

        mutableStateOf(
            activity?.priority
                ?: ActivityPriority.MEDIUM
        )
    }

    var categoryMenuExpanded by
    rememberSaveable {

        mutableStateOf(
            false
        )
    }

    var showDatePicker by
    rememberSaveable {

        mutableStateOf(
            false
        )
    }

    var showTimePicker by
    rememberSaveable {

        mutableStateOf(
            false
        )
    }


    val dateFormatter =
        remember {

            DateTimeFormatter
                .ofPattern(
                    "dd/MM/yyyy"
                )
        }


    val initialDateMillis =
        remember(
            activity?.id
        ) {

            activity
                ?.dueDateEpochDay
                ?.let { epochDay ->

                    LocalDate
                        .ofEpochDay(
                            epochDay
                        )
                        .atStartOfDay(
                            ZoneOffset.UTC
                        )
                        .toInstant()
                        .toEpochMilli()
                }
        }

    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis =
                initialDateMillis
        )


    val currentTime =
        remember {

            LocalTime.now()
        }

    val initialHour =
        activity
            ?.dueTimeMinutes
            ?.div(
                60
            )
            ?: currentTime.hour

    val initialMinute =
        activity
            ?.dueTimeMinutes
            ?.rem(
                60
            )
            ?: currentTime.minute

    val timePickerState =
        rememberTimePickerState(
            initialHour =
                initialHour,

            initialMinute =
                initialMinute,

            is24Hour =
                true
        )

    val selectedCategory =
        categories.firstOrNull { category ->

            category.id ==
                    selectedCategoryId
        }

    LaunchedEffect(
        actionState
    ) {

        val operationSucceeded =
            when {

                !isEditing &&
                        actionState
                                is ActivityActionState.CreateSuccess -> {

                    true
                }

                isEditing &&
                        actionState
                                is ActivityActionState.UpdateSuccess &&
                        actionState.activityId ==
                        activity?.id -> {

                    true
                }

                else -> {

                    false
                }
            }

        if (
            operationSucceeded
        ) {

            onResetActionState()

            onSuccess()
        }
    }

    if (
        showDatePicker
    ) {

        DatePickerDialog(
            onDismissRequest = {

                showDatePicker =
                    false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val selectedMillis =
                            datePickerState
                                .selectedDateMillis

                        if (
                            selectedMillis != null
                        ) {

                            val selectedDate =
                                Instant
                                    .ofEpochMilli(
                                        selectedMillis
                                    )
                                    .atZone(
                                        ZoneOffset.UTC
                                    )
                                    .toLocalDate()

                            selectedDateEpochDay =
                                selectedDate
                                    .toEpochDay()
                        }

                        showDatePicker =
                            false
                    }
                ) {

                    Text(
                        text =
                            "Aceptar"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        showDatePicker =
                            false
                    }
                ) {

                    Text(
                        text =
                            "Cancelar"
                    )
                }
            }
        ) {

            DatePicker(
                state =
                    datePickerState
            )
        }
    }

    if (
        showTimePicker
    ) {

        AlertDialog(
            onDismissRequest = {

                showTimePicker =
                    false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        selectedTimeMinutes =
                            (
                                    timePickerState.hour *
                                            60
                                    ) +
                                    timePickerState.minute

                        showTimePicker =
                            false
                    }
                ) {

                    Text(
                        text =
                            "Aceptar"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        showTimePicker =
                            false
                    }
                ) {

                    Text(
                        text =
                            "Cancelar"
                    )
                }
            },

            title = {

                Text(
                    text =
                        "Seleccionar hora"
                )
            },

            text = {

                TimePicker(
                    state =
                        timePickerState
                )
            }
        )
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    20.dp
                )
    ) {

        TextButton(
            onClick = {

                onResetActionState()

                onBack()
            }
        ) {

            Text(
                text =
                    "← Volver"
            )
        }

        Text(
            text =
                screenTitle,

            fontSize =
                28.sp,

            fontWeight =
                FontWeight.Bold
        )

        Text(
            text =
                screenDescription,

            modifier =
                Modifier.padding(
                    top =
                        4.dp
                ),

            fontSize =
                15.sp
        )

        Spacer(
            modifier =
                Modifier.height(
                    24.dp
                )
        )

        OutlinedTextField(
            value =
                title,

            onValueChange = {
                title =
                    it
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {

                Text(
                    text =
                        "Título *"
                )
            },

            singleLine =
                true
        )

        Spacer(
            modifier =
                Modifier.height(
                    16.dp
                )
        )

        OutlinedTextField(
            value =
                description,

            onValueChange = {

                description =
                    it
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {

                Text(
                    text =
                        "Descripción"
                )
            },

            minLines =
                3,

            maxLines =
                5
        )

        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )

        Text(
            text =
                "Categoría *",

            fontWeight =
                FontWeight.SemiBold
        )

        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )

        Box(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            OutlinedButton(
                onClick = {

                    categoryMenuExpanded =
                        true
                },

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        if (
                            selectedCategory != null
                        ) {

                            "${selectedCategory.icon ?: "•"} " +
                                    selectedCategory.name

                        } else {

                            "Seleccionar categoría"
                        }
                )
            }

            DropdownMenu(
                expanded =
                    categoryMenuExpanded,

                onDismissRequest = {

                    categoryMenuExpanded =
                        false
                }
            ) {

                categories.forEach { category ->

                    DropdownMenuItem(
                        text = {

                            Text(
                                text =
                                    "${category.icon ?: "•"} " +
                                            category.name
                            )
                        },

                        onClick = {

                            selectedCategoryId =
                                category.id

                            categoryMenuExpanded =
                                false
                        }
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )

        Text(
            text =
                "Fecha límite *",

            fontWeight =
                FontWeight.SemiBold
        )

        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )

        OutlinedButton(
            onClick = {

                showDatePicker =
                    true
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                text =
                    selectedDateEpochDay
                        ?.let { epochDay ->

                            LocalDate
                                .ofEpochDay(
                                    epochDay
                                )
                                .format(
                                    dateFormatter
                                )
                        }
                        ?: "Seleccionar fecha"
            )
        }

        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )

        Text(
            text =
                "Hora",

            fontWeight =
                FontWeight.SemiBold
        )

        Text(
            text =
                "Opcional",

            fontSize =
                13.sp
        )

        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )

        OutlinedButton(
            onClick = {

                showTimePicker =
                    true
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                text =
                    selectedTimeMinutes
                        ?.let { minutes ->

                            val hour =
                                minutes / 60

                            val minute =
                                minutes %
                                        60

                            String.format(
                                "%02d:%02d",
                                hour,
                                minute
                            )
                        }
                        ?: "Seleccionar hora"
            )
        }

        if (
            selectedTimeMinutes != null
        ) {

            TextButton(
                onClick = {

                    selectedTimeMinutes =
                        null
                }
            ) {

                Text(
                    text =
                        "Quitar hora"
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )

        Text(
            text =
                "Prioridad *",

            fontWeight =
                FontWeight.SemiBold
        )

        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )
        ) {

            FilterChip(
                selected =
                    selectedPriority ==
                            ActivityPriority.LOW,

                onClick = {

                    selectedPriority =
                        ActivityPriority.LOW
                },

                label = {

                    Text(
                        text =
                            "Baja"
                    )
                }
            )

            FilterChip(
                selected =
                    selectedPriority ==
                            ActivityPriority.MEDIUM,

                onClick = {

                    selectedPriority =
                        ActivityPriority.MEDIUM
                },

                label = {

                    Text(
                        text =
                            "Media"
                    )
                }
            )

            FilterChip(
                selected =
                    selectedPriority ==
                            ActivityPriority.HIGH,

                onClick = {

                    selectedPriority =
                        ActivityPriority.HIGH
                },

                label = {

                    Text(
                        text =
                            "Alta"
                    )
                }
            )
        }

        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )

        if (
            extraContent != null
        ) {

            extraContent()

            Spacer(
                modifier =
                    Modifier.height(
                        20.dp
                    )
            )
        }

        when (
            actionState
        ) {

            is ActivityActionState.ValidationError -> {

                Text(
                    text =
                        actionState.message,

                    color =
                        Color(
                            0xFFFF4B55
                        ),

                    fontSize =
                        14.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )
            }

            is ActivityActionState.Error -> {

                Text(
                    text =
                        actionState.message,

                    color =
                        Color(
                            0xFFFF4B55
                        ),

                    fontSize =
                        14.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )
            }

            else ->
                Unit
        }

        val isProcessing =
            (actionState is ActivityActionState.Saving) ||
                    (actionState is ActivityActionState.Updating)

        Button(
            onClick = {

                onSubmit(
                    title,
                    description,
                    selectedCategoryId,
                    selectedDateEpochDay,
                    selectedTimeMinutes,
                    selectedPriority
                )
            },

            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                !isProcessing
        ) {

            Text(
                text =
                    if (
                        isProcessing
                    ) {

                        loadingButtonText

                    } else {

                        buttonText
                    }
            )
        }

        Spacer(
            modifier =
                Modifier.height(
                    32.dp
                )
        )
    }
}

@Composable
private fun SubtasksSection(
    uiState: SubtasksUiState,
    actionState: SubtaskActionState,
    onCreateSubtask: (String) -> Unit,
    onSetSubtaskCompleted: (
        subtaskId: Long,
        isCompleted: Boolean
    ) -> Unit,
    onUpdateSubtask: (
        subtaskId: Long,
        title: String
    ) -> Unit,
    onDeleteSubtask: (
        subtaskId: Long
    ) -> Unit,
    onResetActionState: () -> Unit
) {

    var newSubtaskTitle by
    rememberSaveable {

        mutableStateOf(
            ""
        )
    }

    var editingSubtaskId by
    rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var editingSubtaskTitle by
    rememberSaveable {
        mutableStateOf("")
    }

    var deletingSubtaskId by
    rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var deletingSubtaskTitle by
    rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(
        actionState
    ) {

        when (actionState) {

            is SubtaskActionState.CreateSuccess -> {

                newSubtaskTitle = ""

                onResetActionState()
            }

            is SubtaskActionState.CompletionSuccess -> {

                onResetActionState()
            }

            is SubtaskActionState.UpdateSuccess -> {

                editingSubtaskId = null
                editingSubtaskTitle = ""

                onResetActionState()
            }

            is SubtaskActionState.DeleteSuccess -> {

                deletingSubtaskId = null
                deletingSubtaskTitle = ""

                onResetActionState()
            }

            else -> Unit
        }
    }

    if (editingSubtaskId != null) {

        val isUpdating =
            (actionState is SubtaskActionState.Updating)

        AlertDialog(
            onDismissRequest = {

                if (!isUpdating) {

                    editingSubtaskId = null
                    editingSubtaskTitle = ""
                }
            },

            title = {

                Text(
                    text = "Editar subtarea"
                )
            },

            text = {

                OutlinedTextField(
                    value =
                        editingSubtaskTitle,

                    onValueChange = {

                        editingSubtaskTitle = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {

                        Text(
                            text = "Nombre"
                        )
                    },

                    singleLine = true
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val subtaskId =
                            editingSubtaskId

                        if (subtaskId != null) {

                            onUpdateSubtask(
                                subtaskId,
                                editingSubtaskTitle
                            )
                        }
                    },

                    enabled =
                        !isUpdating
                ) {

                    Text(
                        text =
                            if (isUpdating) {
                                "Guardando..."
                            } else {
                                "Guardar"
                            }
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        editingSubtaskId = null
                        editingSubtaskTitle = ""
                    },

                    enabled =
                        !isUpdating
                ) {

                    Text(
                        text = "Cancelar"
                    )
                }
            }
        )
    }

    if (deletingSubtaskId != null) {

        val isDeleting =
            (actionState is SubtaskActionState.Deleting)

        AlertDialog(
            onDismissRequest = {

                if (!isDeleting) {

                    deletingSubtaskId = null
                    deletingSubtaskTitle = ""
                }
            },

            title = {

                Text(
                    text = "¿Eliminar subtarea?"
                )
            },

            text = {

                Text(
                    text =
                        "¿Quieres eliminar \"$deletingSubtaskTitle\"?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val subtaskId =
                            deletingSubtaskId

                        if (subtaskId != null) {

                            onDeleteSubtask(
                                subtaskId
                            )
                        }
                    },

                    enabled =
                        !isDeleting
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
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        deletingSubtaskId = null
                        deletingSubtaskTitle = ""
                    },

                    enabled =
                        !isDeleting
                ) {

                    Text(
                        text = "Cancelar"
                    )
                }
            }
        )
    }

    Text(
        text =
            "Subtareas",

        fontSize =
            19.sp,

        fontWeight =
            FontWeight.Bold
    )

    Spacer(
        modifier =
            Modifier.height(
                8.dp
            )
    )

    when (
        uiState
    ) {

        SubtasksUiState.Idle,
        SubtasksUiState.Loading -> {

            Text(
                text =
                    "Cargando subtareas..."
            )
        }

        is SubtasksUiState.Success -> {

            if (
                uiState.subtasks.isEmpty()
            ) {

                Text(
                    text =
                        "Todavía no hay subtareas."
                )

            } else {

                uiState.subtasks.forEach { subtask ->

                    val isChangingCompletion =
                        (actionState is SubtaskActionState.ChangingCompletion) &&
                                (actionState.subtaskId == subtask.id)

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalAlignment =
                            androidx.compose.ui.Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked =
                                subtask.isCompleted,

                            onCheckedChange = { checked ->

                                onSetSubtaskCompleted(
                                    subtask.id,
                                    checked
                                )
                            },

                            enabled =
                                !isChangingCompletion
                        )

                        Text(
                            text =
                                subtask.title,

                            modifier =
                                Modifier.weight(1f),

                            fontWeight =
                                if (
                                    subtask.isCompleted
                                ) {

                                    FontWeight.Normal

                                } else {

                                    FontWeight.Medium
                                }
                        )
                    }

                    OutlinedButton(
                        onClick = {

                            editingSubtaskId =
                                subtask.id

                            editingSubtaskTitle =
                                subtask.title
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        enabled =
                            actionState !is SubtaskActionState.Updating &&
                                    actionState !is SubtaskActionState.Deleting
                    ) {

                        Text(
                            text = "Editar subtarea"
                        )
                    }

                    OutlinedButton(
                        onClick = {

                            deletingSubtaskId =
                                subtask.id

                            deletingSubtaskTitle =
                                subtask.title
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        enabled =
                            (actionState !is SubtaskActionState.Updating) &&
                                    (actionState !is SubtaskActionState.Deleting)
                    ) {

                        Text(
                            text = "Eliminar subtarea"
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                }
            }
        }

        SubtasksUiState.NoActiveSession -> {

            Text(
                text =
                    "No hay una sesión activa.",

                color =
                    Color(
                        0xFFFF4B55
                    )
            )
        }

        is SubtasksUiState.Error -> {

            Text(
                text =
                    uiState.message,

                color =
                    Color(
                        0xFFFF4B55
                    )
            )
        }
    }

    Spacer(
        modifier =
            Modifier.height(
                12.dp
            )
    )

    OutlinedTextField(
        value =
            newSubtaskTitle,

        onValueChange = {

            newSubtaskTitle =
                it
        },

        modifier =
            Modifier.fillMaxWidth(),

        label = {

            Text(
                text =
                    "Nueva subtarea"
            )
        },

        placeholder = {

            Text(
                text =
                    "Ej. Resolver ejercicios"
            )
        },

        singleLine =
            true
    )

    if (
        actionState
                is SubtaskActionState.ValidationError
    ) {

        Text(
            text =
                actionState.message,

            modifier =
                Modifier.padding(
                    top =
                        6.dp
                ),

            color =
                Color(
                    0xFFFF4B55
                ),

            fontSize =
                14.sp
        )
    }

    if (
        actionState
                is SubtaskActionState.Error
    ) {

        Text(
            text =
                actionState.message,

            modifier =
                Modifier.padding(
                    top =
                        6.dp
                ),

            color =
                Color(
                    0xFFFF4B55
                ),

            fontSize =
                14.sp
        )
    }

    Spacer(
        modifier =
            Modifier.height(
                8.dp
            )
    )

    val isCreating =
        (actionState is SubtaskActionState.Creating)

    Button(
        onClick = {

            onCreateSubtask(
                newSubtaskTitle
            )
        },

        modifier =
            Modifier.fillMaxWidth(),

        enabled =
            !isCreating &&
                    (uiState is SubtasksUiState.Success)
    ) {

        Text(
            text =
                if (
                    isCreating
                ) {

                    "Agregando..."

                } else {

                    "+ Agregar subtarea"
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RemindersSection(
    uiState: ReminderUiState,
    actionState: ReminderActionState,
    defaultTitle: String,
    onCreateReminder: (
        title: String,
        message: String,
        triggerAtMillis: Long
    ) -> Unit,
    onCancelReminder: (
        reminderId: Long
    ) -> Unit
) {

    val context =
        LocalContext.current

    var reminderTitle by
    rememberSaveable {
        mutableStateOf(
            defaultTitle
        )
    }

    var reminderMessage by
    rememberSaveable {
        mutableStateOf("")
    }

    var selectedDateEpochDay by
    rememberSaveable {
        mutableStateOf<Long?>(
            null
        )
    }

    var selectedTimeMinutes by
    rememberSaveable {
        mutableStateOf<Int?>(
            null
        )
    }

    var showDatePicker by
    rememberSaveable {
        mutableStateOf(false)
    }

    var showTimePicker by
    rememberSaveable {
        mutableStateOf(false)
    }

    var localError by
    rememberSaveable {
        mutableStateOf<String?>(
            null
        )
    }

    var pendingTitle by
    rememberSaveable {
        mutableStateOf<String?>(
            null
        )
    }

    var pendingMessage by
    rememberSaveable {
        mutableStateOf<String?>(
            null
        )
    }

    var pendingTriggerAtMillis by
    rememberSaveable {
        mutableStateOf<Long?>(
            null
        )
    }

    val currentTime =
        remember {
            LocalTime.now()
        }

    val datePickerState =
        rememberDatePickerState()

    val timePickerState =
        rememberTimePickerState(
            initialHour =
                currentTime.hour,

            initialMinute =
                currentTime.minute,

            is24Hour =
                true
        )

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .RequestPermission()
        ) { granted ->

            if (granted) {

                val title =
                    pendingTitle

                val triggerAtMillis =
                    pendingTriggerAtMillis

                if (
                    title != null &&
                    triggerAtMillis != null
                ) {

                    onCreateReminder(
                        title,
                        pendingMessage.orEmpty(),
                        triggerAtMillis
                    )
                }

            } else {

                localError =
                    "Debes permitir las notificaciones para recibir este recordatorio."
            }

            pendingTitle =
                null

            pendingMessage =
                null

            pendingTriggerAtMillis =
                null
        }

    LaunchedEffect(
        actionState
    ) {

        if (
            actionState
                    is ReminderActionState.CreateSuccess
        ) {

            reminderMessage =
                ""

            selectedDateEpochDay =
                null

            selectedTimeMinutes =
                null

            localError =
                null
        }
    }

    if (showDatePicker) {

        DatePickerDialog(
            onDismissRequest = {

                showDatePicker =
                    false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val selectedMillis =
                            datePickerState
                                .selectedDateMillis

                        if (
                            selectedMillis != null
                        ) {

                            selectedDateEpochDay =
                                Instant
                                    .ofEpochMilli(
                                        selectedMillis
                                    )
                                    .atZone(
                                        ZoneOffset.UTC
                                    )
                                    .toLocalDate()
                                    .toEpochDay()
                        }

                        showDatePicker =
                            false
                    }
                ) {

                    Text(
                        text = "Aceptar"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        showDatePicker =
                            false
                    }
                ) {

                    Text(
                        text = "Cancelar"
                    )
                }
            }
        ) {

            DatePicker(
                state =
                    datePickerState
            )
        }
    }

    if (showTimePicker) {

        AlertDialog(
            onDismissRequest = {

                showTimePicker =
                    false
            },

            title = {

                Text(
                    text =
                        "Hora del recordatorio"
                )
            },

            text = {

                TimePicker(
                    state =
                        timePickerState
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        selectedTimeMinutes =
                            (
                                    timePickerState.hour *
                                            60
                                    ) +
                                    timePickerState.minute

                        showTimePicker =
                            false
                    }
                ) {

                    Text(
                        text = "Aceptar"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        showTimePicker =
                            false
                    }
                ) {

                    Text(
                        text = "Cancelar"
                    )
                }
            }
        )
    }

    Text(
        text = "Recordatorios",
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(
        modifier =
            Modifier.height(8.dp)
    )

    when (uiState) {

        ReminderUiState.Idle,
        ReminderUiState.Loading -> {

            Text(
                text =
                    "Cargando recordatorios..."
            )
        }

        is ReminderUiState.Success -> {

            if (
                uiState.reminders.isEmpty()
            ) {

                Text(
                    text =
                        "Todavía no hay recordatorios programados."
                )

            } else {

                uiState.reminders
                    .forEach { reminder ->

                        val reminderDateTime =
                            Instant
                                .ofEpochMilli(
                                    reminder.triggerAtMillis
                                )
                                .atZone(
                                    ZoneId.systemDefault()
                                )

                        val formattedDate =
                            reminderDateTime
                                .format(
                                    DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy"
                                    )
                                )

                        val formattedTime =
                            reminderDateTime
                                .format(
                                    DateTimeFormatter.ofPattern(
                                        "HH:mm"
                                    )
                                )

                        Text(
                            text =
                                "• ${reminder.title}",

                            fontWeight =
                                FontWeight.Medium
                        )

                        Text(
                            text =
                                "$formattedDate · $formattedTime",

                            fontSize =
                                13.sp
                        )

                        Text(
                            text =
                                when (
                                    reminder.status
                                ) {

                                    "SCHEDULED" ->
                                        "Programado"

                                    "DELIVERED" ->
                                        "Entregado"

                                    "CANCELLED" ->
                                        "Cancelado"

                                    else ->
                                        reminder.status
                                },

                            fontSize =
                                13.sp
                        )

                        if (
                            reminder.status ==
                            "SCHEDULED"
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            TextButton(
                                onClick = {

                                    onCancelReminder(
                                        reminder.id
                                    )
                                },

                                enabled =
                                    actionState
                                            !is ReminderActionState.Cancelling
                            ) {

                                Text(
                                    text =
                                        if (
                                            actionState
                                                    is ReminderActionState.Cancelling &&
                                            actionState.reminderId ==
                                            reminder.id
                                        ) {

                                            "Cancelando..."

                                        } else {

                                            "Cancelar recordatorio"
                                        }
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )
                    }
            }
        }

        ReminderUiState.NoActiveSession -> {

            Text(
                text =
                    "No hay una sesión activa.",

                color =
                    Color(
                        0xFFFF4B55
                    )
            )
        }

        is ReminderUiState.Error -> {

            Text(
                text =
                    uiState.message,

                color =
                    Color(
                        0xFFFF4B55
                    )
            )
        }
    }

    Spacer(
        modifier =
            Modifier.height(16.dp)
    )

    Text(
        text =
            "Nuevo recordatorio",

        fontWeight =
            FontWeight.SemiBold
    )

    Spacer(
        modifier =
            Modifier.height(8.dp)
    )

    OutlinedTextField(
        value =
            reminderTitle,

        onValueChange = {

            reminderTitle =
                it

            localError =
                null
        },

        modifier =
            Modifier.fillMaxWidth(),

        label = {

            Text(
                text = "Título"
            )
        },

        singleLine =
            true
    )

    Spacer(
        modifier =
            Modifier.height(10.dp)
    )

    OutlinedTextField(
        value =
            reminderMessage,

        onValueChange = {

            reminderMessage =
                it
        },

        modifier =
            Modifier.fillMaxWidth(),

        label = {

            Text(
                text = "Mensaje"
            )
        },

        minLines =
            2,

        maxLines =
            4
    )

    Spacer(
        modifier =
            Modifier.height(12.dp)
    )

    OutlinedButton(
        onClick = {

            showDatePicker =
                true
        },

        modifier =
            Modifier.fillMaxWidth()
    ) {

        Text(
            text =
                selectedDateEpochDay
                    ?.let { epochDay ->

                        LocalDate
                            .ofEpochDay(
                                epochDay
                            )
                            .format(
                                DateTimeFormatter
                                    .ofPattern(
                                        "dd/MM/yyyy"
                                    )
                            )
                    }
                    ?: "Seleccionar fecha"
        )
    }

    Spacer(
        modifier =
            Modifier.height(8.dp)
    )

    OutlinedButton(
        onClick = {

            showTimePicker =
                true
        },

        modifier =
            Modifier.fillMaxWidth()
    ) {

        Text(
            text =
                selectedTimeMinutes
                    ?.let { minutes ->

                        String.format(
                            "%02d:%02d",
                            minutes / 60,
                            minutes % 60
                        )
                    }
                    ?: "Seleccionar hora"
        )
    }

    if (localError != null) {

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                localError.orEmpty(),

            color =
                Color(
                    0xFFFF4B55
                ),

            fontSize =
                14.sp
        )
    }

    when (actionState) {

        is ReminderActionState.ValidationError -> {

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    actionState.message,

                color =
                    Color(
                        0xFFFF4B55
                    ),

                fontSize =
                    14.sp
            )
        }

        is ReminderActionState.Error -> {

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    actionState.message,

                color =
                    Color(
                        0xFFFF4B55
                    ),

                fontSize =
                    14.sp
            )
        }

        is ReminderActionState.CreateSuccess -> {

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    if (
                        actionState.isExact
                    ) {

                        "Recordatorio programado correctamente."

                    } else {

                        "Recordatorio programado. Android puede entregarlo ligeramente después de la hora indicada."
                    },

                fontSize =
                    14.sp
            )
        }

        is ReminderActionState.CancelSuccess -> {

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "Recordatorio cancelado correctamente.",

                fontSize =
                    14.sp
            )
        }

        else ->
            Unit
    }

    Spacer(
        modifier =
            Modifier.height(12.dp)
    )

    val isCreating =
        (actionState is ReminderActionState.Creating)

    Button(
        onClick = {

            localError =
                null

            val dateEpochDay =
                selectedDateEpochDay

            val timeMinutes =
                selectedTimeMinutes

            if (
                reminderTitle
                    .trim()
                    .isBlank()
            ) {

                localError =
                    "Escribe un título para el recordatorio."

                return@Button
            }

            if (
                dateEpochDay == null
            ) {

                localError =
                    "Selecciona una fecha."

                return@Button
            }

            if (
                timeMinutes == null
            ) {

                localError =
                    "Selecciona una hora."

                return@Button
            }

            val localDate =
                LocalDate.ofEpochDay(
                    dateEpochDay
                )

            val hour =
                timeMinutes / 60

            val minute =
                timeMinutes % 60

            val triggerAtMillis =
                localDate
                    .atTime(
                        hour,
                        minute
                    )
                    .atZone(
                        ZoneId.systemDefault()
                    )
                    .toInstant()
                    .toEpochMilli()

            if (
                triggerAtMillis <=
                System.currentTimeMillis()
            ) {

                localError =
                    "Selecciona una fecha y hora futuras."

                return@Button
            }

            if (
                NotificationPermission
                    .isGranted(
                        context
                    )
            ) {

                onCreateReminder(
                    reminderTitle,
                    reminderMessage,
                    triggerAtMillis
                )

            } else {

                pendingTitle =
                    reminderTitle

                pendingMessage =
                    reminderMessage

                pendingTriggerAtMillis =
                    triggerAtMillis

                notificationPermissionLauncher
                    .launch(
                        Manifest.permission
                            .POST_NOTIFICATIONS
                    )
            }
        },

        modifier =
            Modifier.fillMaxWidth(),

        enabled =
            !isCreating &&
                    (uiState is ReminderUiState.Success)
    ) {

        Text(
            text =
                if (
                    isCreating
                ) {

                    "Programando..."

                } else {

                    "Programar recordatorio"
                }
        )
    }
}
