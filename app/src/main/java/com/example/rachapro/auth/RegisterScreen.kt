package com.example.rachapro.auth

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.rachapro.ui.components.RachaBrandMark
import com.example.rachapro.ui.components.RachaCard
import com.example.rachapro.ui.components.RachaErrorMessage
import com.example.rachapro.ui.components.RachaPrimaryButton
import com.example.rachapro.ui.components.RachaSectionHeader
import com.example.rachapro.ui.components.RachaSoftGradient
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onBack: () -> Unit,
    onLogin: () -> Unit,

    onRegister: (
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        semester: Int?,
        acceptedPrivacyPolicy: Boolean
    ) -> Unit,

    onRegistrationSuccess: () -> Unit
) {

    var fullName by rememberSaveable {
        mutableStateOf("")
    }

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var confirmPassword by rememberSaveable {
        mutableStateOf("")
    }

    var semester by rememberSaveable {
        mutableStateOf("")
    }

    var acceptPrivacy by rememberSaveable {
        mutableStateOf(false)
    }

    var showPassword by rememberSaveable {
        mutableStateOf(false)
    }

    var showConfirmPassword by rememberSaveable {
        mutableStateOf(false)
    }

    var semesterMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(uiState) {

        if (uiState is AuthUiState.RegistrationSuccess) {
            onRegistrationSuccess()
        }
    }

    val semesters = (1..10).map {
        "Semestre $it"
    }

    val isLoading = uiState is AuthUiState.Loading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(RachaSoftGradient)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 30.dp)
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            RachaBrandMark(size = 56.dp)

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Empieza tu racha académica con el pie derecho.",
                style = MaterialTheme.typography.bodyMedium,
                color = RachaOnSurfaceMuted
            )

            Spacer(modifier = Modifier.height(22.dp))

            RachaCard {
                Column {

                    RachaSectionHeader(
                        title = "Datos personales",
                        supportingText = "Así te reconoceremos en la app"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FieldLabel(text = "Nombre completo", required = true)

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                        },
                        placeholder = { Text("Juan Pérez") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.small
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FieldLabel(text = "Correo electrónico", required = true)

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                        },
                        placeholder = { Text("correo@universidad.edu.co") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        shape = MaterialTheme.shapes.small
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FieldLabel(text = "Semestre", required = true)

                    Box(modifier = Modifier.fillMaxWidth()) {

                        OutlinedButton(
                            onClick = {
                                semesterMenuExpanded = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = MaterialTheme.shapes.small
                        ) {

                            Text(
                                text = if (semester.isEmpty()) {
                                    "Selecciona tu semestre"
                                } else {
                                    semester
                                },
                                modifier = Modifier.weight(1f),
                                color = if (semester.isEmpty()) {
                                    RachaOnSurfaceMuted
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )

                            Text(
                                text = "⌄",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = semesterMenuExpanded,
                            onDismissRequest = {
                                semesterMenuExpanded = false
                            }
                        ) {

                            semesters.forEach { option ->

                                DropdownMenuItem(
                                    text = {
                                        Text(option)
                                    },
                                    onClick = {
                                        semester = option
                                        semesterMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            RachaCard {
                Column {

                    RachaSectionHeader(
                        title = "Seguridad",
                        supportingText = "Protege el acceso a tu cuenta"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FieldLabel(text = "Contraseña", required = true)

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        placeholder = { Text("Mínimo 8 caracteres") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        visualTransformation = if (showPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            TextButton(
                                onClick = {
                                    showPassword = !showPassword
                                }
                            ) {
                                Text(
                                    text = if (showPassword) {
                                        "Ocultar"
                                    } else {
                                        "Ver"
                                    },
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        },
                        shape = MaterialTheme.shapes.small
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FieldLabel(text = "Confirmar contraseña", required = true)

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                        },
                        placeholder = { Text("Repite tu contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        visualTransformation = if (showConfirmPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            TextButton(
                                onClick = {
                                    showConfirmPassword = !showConfirmPassword
                                }
                            ) {
                                Text(
                                    text = if (showConfirmPassword) {
                                        "Ocultar"
                                    } else {
                                        "Ver"
                                    },
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        },
                        shape = MaterialTheme.shapes.small
                    )

                    if (confirmPassword.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (password == confirmPassword) {
                                "Las contraseñas coinciden."
                            } else {
                                "Las contraseñas todavía no coinciden."
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (password == confirmPassword) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .clickable { acceptPrivacy = !acceptPrivacy }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {

                        Checkbox(
                            checked = acceptPrivacy,
                            onCheckedChange = {
                                acceptPrivacy = it
                            }
                        )

                        Text(
                            text = "Acepto el tratamiento de datos personales y la política de privacidad",
                            modifier = Modifier.padding(top = 12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = RachaOnSurfaceMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            RachaPrimaryButton(
                text = if (isLoading) {
                    "Creando cuenta..."
                } else {
                    "Crear cuenta"
                },
                onClick = {

                    val semesterNumber = semester
                        .removePrefix("Semestre ")
                        .toIntOrNull()

                    onRegister(
                        fullName,
                        email,
                        password,
                        confirmPassword,
                        semesterNumber,
                        acceptPrivacy
                    )
                },
                enabled = fullName.isNotBlank() &&
                    email.isNotBlank() &&
                    password.length >= 8 &&
                    password == confirmPassword &&
                    semester.isNotBlank() &&
                    acceptPrivacy &&
                    !isLoading,
                icon = Icons.Filled.Check
            )

            when (uiState) {

                AuthUiState.EmailAlreadyRegistered -> {

                    Spacer(modifier = Modifier.height(12.dp))

                    RachaErrorMessage(
                        message = "Ya existe una cuenta registrada con este correo."
                    )
                }

                is AuthUiState.ValidationError -> {

                    Spacer(modifier = Modifier.height(12.dp))

                    RachaErrorMessage(
                        message = uiState.message
                    )
                }

                AuthUiState.Error -> {

                    Spacer(modifier = Modifier.height(12.dp))

                    RachaErrorMessage(
                        message = "No fue posible crear la cuenta. Inténtalo nuevamente."
                    )
                }

                else -> Unit
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "¿Ya tienes cuenta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RachaOnSurfaceMuted
                )

                TextButton(onClick = onLogin) {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.labelLarge,
                        color = RachaIndigo
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(
    text: String,
    required: Boolean = false
) {

    Row(verticalAlignment = Alignment.CenterVertically) {

        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (required) {

            Text(
                text = " *",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    Spacer(modifier = Modifier.height(6.dp))
}
