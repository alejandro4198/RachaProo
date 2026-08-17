package com.example.rachapro.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.rachapro.domain.AchievementEngine
import com.example.rachapro.ui.components.RachaCard
import com.example.rachapro.ui.components.RachaGradientHeader
import com.example.rachapro.ui.components.RachaLoadingScreen
import com.example.rachapro.ui.components.RachaPrimaryButton
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
                    modifier = Modifier.fillMaxSize().padding(padding),
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
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditProfileDialog(
            initialName = state.profileDraftName,
            initialSemester = state.profileDraftSemester,
            isSaving = saveState is ProfileSaveState.Saving,
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
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        RachaGradientHeader(title = "Perfil", subtitle = state.fullName)

        Spacer(modifier = Modifier.height(16.dp))

        RachaCard {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = state.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(text = state.email, style = MaterialTheme.typography.bodyMedium, color = RachaOnSurfaceMuted, modifier = Modifier.padding(top = 4.dp))
                        Text(text = "Semestre ${state.semester}", style = MaterialTheme.typography.labelLarge, color = RachaIndigo, modifier = Modifier.padding(top = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = { showEditDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Editar perfil")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("Pomodoro")
        Spacer(modifier = Modifier.height(12.dp))

        PomodoroPreferenceField("Enfoque (min)", state.pomodoroDraft.focusMinutes) { v ->
            onPomodoroDraftChange(v, state.pomodoroDraft.shortBreakMinutes, state.pomodoroDraft.longBreakMinutes)
        }
        Spacer(modifier = Modifier.height(8.dp))
        PomodoroPreferenceField("Descanso corto (min)", state.pomodoroDraft.shortBreakMinutes) { v ->
            onPomodoroDraftChange(state.pomodoroDraft.focusMinutes, v, state.pomodoroDraft.longBreakMinutes)
        }
        Spacer(modifier = Modifier.height(8.dp))
        PomodoroPreferenceField("Descanso largo (min)", state.pomodoroDraft.longBreakMinutes) { v ->
            onPomodoroDraftChange(state.pomodoroDraft.focusMinutes, state.pomodoroDraft.shortBreakMinutes, v)
        }

        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("Notificaciones")
        Spacer(modifier = Modifier.height(12.dp))

        RachaCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Recordatorios activos", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = if (notificationsPermissionGranted) "Permiso concedido." else "Activa el permiso en ajustes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = RachaOnSurfaceMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Switch(
                    checked = state.notificationsEnabled,
                    onCheckedChange = onNotificationsEnabledChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = RachaIndigo, checkedTrackColor = RachaIndigo.copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        RachaPrimaryButton(
            text = if (saveState is ProfileSaveState.Saving) "Guardando..." else "Guardar preferencias",
            onClick = onSavePreferences,
            enabled = saveState !is ProfileSaveState.Saving
        )

        Spacer(modifier = Modifier.height(28.dp))
        SectionTitle("Logros")
        Spacer(modifier = Modifier.height(12.dp))

        state.allAchievementTypes.forEach { type ->
            val unlocked = type in unlockedTypes
            RachaCard {
                Column {
                    Text(
                        text = if (unlocked) "🏅 ${AchievementEngine.titleFor(type)}" else "🔒 ${AchievementEngine.titleFor(type)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (unlocked) RachaSuccess else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = AchievementEngine.descriptionFor(type),
                        style = MaterialTheme.typography.bodySmall,
                        color = RachaOnSurfaceMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(onClick = onLogout, enabled = !isLoggingOut, modifier = Modifier.fillMaxWidth()) {
            Text(if (isLoggingOut) "Cerrando sesión..." else "Cerrar sesión")
        }
        Spacer(modifier = Modifier.height(24.dp))
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
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun PomodoroPreferenceField(label: String, value: Int, onValueChange: (Int) -> Unit) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { text ->
            val parsed = text.filter { it.isDigit() }.toIntOrNull()
            if (parsed != null) onValueChange(parsed) else if (text.isEmpty()) onValueChange(1)
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
private fun ProfileErrorContent(modifier: Modifier, message: String, onRetry: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message)
        Spacer(modifier = Modifier.height(16.dp))
        RachaPrimaryButton(text = "Reintentar", onClick = onRetry)
    }
}
