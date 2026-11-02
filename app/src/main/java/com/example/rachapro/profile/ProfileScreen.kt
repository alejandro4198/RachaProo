package com.example.rachapro.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.domain.AchievementEngine
import com.example.rachapro.ui.components.RachaAnimatedProgress
import com.example.rachapro.ui.components.RachaAvatar
import com.example.rachapro.ui.components.RachaCard
import com.example.rachapro.ui.components.RachaGradientHeader
import com.example.rachapro.ui.components.RachaLoadingScreen
import com.example.rachapro.ui.components.RachaPrimaryButton
import com.example.rachapro.ui.components.RachaSectionHeader
import com.example.rachapro.ui.theme.RachaAmber
import com.example.rachapro.ui.theme.RachaError
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted
import com.example.rachapro.ui.theme.RachaSuccess

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    saveState: ProfileSaveState,
    isLoggingOut: Boolean,
    notificationsPermissionGranted: Boolean,
    onRetry: () -> Unit,
    onPomodoroDraftChange: (
        focusMinutes: Int,
        shortBreakMinutes: Int,
        longBreakMinutes: Int
    ) -> Unit,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    onSavePreferences: () -> Unit,
    onProfileDraftChange: (String, Int) -> Unit,
    onSaveProfile: () -> Unit,
    onResetSaveState: () -> Unit,
    onLogout: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(saveState) {
        when (saveState) {
            is ProfileSaveState.Success -> {
                snackbarHostState.showSnackbar(saveState.message)
                onResetSaveState()
            }
            is ProfileSaveState.Error -> {
                snackbarHostState.showSnackbar(saveState.message)
                onResetSaveState()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when (uiState) {
            ProfileUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RachaLoadingScreen("Cargando perfil...")
                }
            }

            is ProfileUiState.Success -> {
                ProfileSuccessContent(
                    modifier = Modifier.padding(padding),
                    state = uiState,
                    saveState = saveState,
                    isLoggingOut = isLoggingOut,
                    notificationsPermissionGranted = notificationsPermissionGranted,
                    onPomodoroDraftChange = onPomodoroDraftChange,
                    onNotificationsEnabledChange = onNotificationsEnabledChange,
                    onSavePreferences = onSavePreferences,
                    onProfileDraftChange = onProfileDraftChange,
                    onSaveProfile = onSaveProfile,
                    onLogout = onLogout
                )
            }

            ProfileUiState.NoActiveSession -> {
                ProfileErrorContent(Modifier.padding(padding), "No hay una sesión activa.", onRetry)
            }

            ProfileUiState.Error -> {
                ProfileErrorContent(Modifier.padding(padding), "No fue posible cargar el perfil.", onRetry)
            }
        }
    }
}

@Composable
private fun ProfileSuccessContent(
    modifier: Modifier = Modifier,
    state: ProfileUiState.Success,
    saveState: ProfileSaveState,
    isLoggingOut: Boolean,
    notificationsPermissionGranted: Boolean,
    onPomodoroDraftChange: (Int, Int, Int) -> Unit,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    onSavePreferences: () -> Unit,
    onProfileDraftChange: (String, Int) -> Unit,
    onSaveProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val unlockedTypes = state.achievements.map { it.type }.toSet()
    val totalAchievements = state.allAchievementTypes.size.coerceAtLeast(1)
    val unlockedCount = state.allAchievementTypes.count { it in unlockedTypes }
    var showEditDialog by remember { mutableStateOf(false) }
    val isSaving = saveState is ProfileSaveState.Saving

    if (showEditDialog) {
        EditProfileDialog(
            initialName = state.profileDraftName,
            initialSemester = state.profileDraftSemester,
            isSaving = isSaving,
            onDismiss = { showEditDialog = false },
            onSave = { name, semester ->
                onProfileDraftChange(name, semester)
                onSaveProfile()
                showEditDialog = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        RachaGradientHeader(
            title = "Perfil",
            eyebrow = "Tu espacio",
            subtitle = "Personaliza tu ritmo y celebra tus logros."
        )

        Spacer(modifier = Modifier.height(16.dp))

        RachaCard(modifier = Modifier.padding(0.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                RachaAvatar(name = state.fullName, size = 62.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Text(
                        text = state.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        text = state.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = RachaOnSurfaceMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(RachaIndigo.copy(alpha = 0.10f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎓 Semestre ${state.semester}",
                            style = MaterialTheme.typography.labelMedium,
                            color = RachaIndigo
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                OutlinedButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.padding(4.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Editar perfil")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        RachaCard(containerColor = RachaIndigo.copy(alpha = 0.04f), border = null) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(RachaAmber.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏅", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Logros desbloqueados",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "$unlockedCount de $totalAchievements",
                            style = MaterialTheme.typography.bodySmall,
                            color = RachaOnSurfaceMuted
                        )
                    }
                    Text(
                        text = "${(unlockedCount * 100 / totalAchievements)}%",
                        style = MaterialTheme.typography.headlineSmall,
                        color = RachaIndigo
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                RachaAnimatedProgress(
                    progress = unlockedCount.toFloat() / totalAchievements.toFloat(),
                    showPercentage = false
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        RachaSectionHeader(
            title = "Preferencias",
            supportingText = "Ajusta tu ritmo de estudio"
        )

        Spacer(modifier = Modifier.height(12.dp))

        RachaCard {
            Column {
                Text(
                    text = "Pomodoro",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Define cuánto duran tus ciclos de enfoque y descanso.",
                    style = MaterialTheme.typography.bodySmall,
                    color = RachaOnSurfaceMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PomodoroPreferenceField(
                        label = "Enfoque",
                        value = state.pomodoroDraft.focusMinutes,
                        modifier = Modifier.weight(1f)
                    ) { v ->
                        onPomodoroDraftChange(v, state.pomodoroDraft.shortBreakMinutes, state.pomodoroDraft.longBreakMinutes)
                    }
                    PomodoroPreferenceField(
                        label = "Corto",
                        value = state.pomodoroDraft.shortBreakMinutes,
                        modifier = Modifier.weight(1f)
                    ) { v ->
                        onPomodoroDraftChange(state.pomodoroDraft.focusMinutes, v, state.pomodoroDraft.longBreakMinutes)
                    }
                    PomodoroPreferenceField(
                        label = "Largo",
                        value = state.pomodoroDraft.longBreakMinutes,
                        modifier = Modifier.weight(1f)
                    ) { v ->
                        onPomodoroDraftChange(state.pomodoroDraft.focusMinutes, state.pomodoroDraft.shortBreakMinutes, v)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Valores en minutos.",
                    style = MaterialTheme.typography.labelSmall,
                    color = RachaOnSurfaceMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        RachaCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Recordatorios activos",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = if (notificationsPermissionGranted) {
                            "Permiso concedido."
                        } else {
                            "Activa el permiso en ajustes del sistema."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = RachaOnSurfaceMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Switch(
                    checked = state.notificationsEnabled,
                    onCheckedChange = onNotificationsEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = RachaIndigo,
                        checkedTrackColor = RachaIndigo.copy(alpha = 0.4f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        RachaPrimaryButton(
            text = if (isSaving) "Guardando..." else "Guardar preferencias",
            onClick = onSavePreferences,
            enabled = !isSaving
        )

        Spacer(modifier = Modifier.height(28.dp))

        RachaSectionHeader(
            title = "Logros",
            supportingText = "Cada paso cuenta. Desbloquea el siguiente."
        )

        Spacer(modifier = Modifier.height(12.dp))

        state.allAchievementTypes.forEach { type ->
            val unlocked = type in unlockedTypes
            AchievementRow(
                title = AchievementEngine.titleFor(type),
                description = AchievementEngine.descriptionFor(type),
                unlocked = unlocked
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        RachaSectionHeader(
            title = "Cuenta",
            supportingText = "Gestiona tu sesión en este dispositivo"
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLogout,
            enabled = !isLoggingOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(RachaIndigo.copy(alpha = 0.10f)),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = if (isLoggingOut) "Cerrando sesión..." else "Cerrar sesión",
                color = RachaError,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun AchievementRow(
    title: String,
    description: String,
    unlocked: Boolean
) {
    RachaCard(
        containerColor = if (unlocked) {
            RachaSuccess.copy(alpha = 0.05f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (unlocked) RachaSuccess.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (unlocked) "🏅" else "🔒",
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (unlocked) RachaSuccess else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = RachaOnSurfaceMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Text(
                text = if (unlocked) "Listo" else "Pendiente",
                style = MaterialTheme.typography.labelSmall,
                color = if (unlocked) RachaSuccess else RachaOnSurfaceMuted
            )
        }
    }
}

@Composable
private fun EditProfileDialog(
    initialName: String,
    initialSemester: Int,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var semesterText by remember(initialSemester) { mutableStateOf(initialSemester.toString()) }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text("Editar perfil") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = semesterText,
                    onValueChange = { semesterText = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("Semestre (1-10)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val semester = semesterText.toIntOrNull() ?: 0
                    onSave(name, semester)
                },
                enabled = !isSaving
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) { Text("Cancelar") }
        }
    )
}

@Composable
private fun PomodoroPreferenceField(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit
) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { text ->
            val parsed = text.filter { it.isDigit() }.toIntOrNull()
            if (parsed != null) onValueChange(parsed) else if (text.isEmpty()) onValueChange(1)
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier,
        shape = MaterialTheme.shapes.small
    )
}

@Composable
private fun ProfileErrorContent(modifier: Modifier, message: String, onRetry: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RachaCard {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "👤", fontSize = 32.sp)
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
