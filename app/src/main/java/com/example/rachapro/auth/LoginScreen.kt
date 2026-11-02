package com.example.rachapro.auth

import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.ui.components.RachaBrandMark
import com.example.rachapro.ui.components.RachaCard
import com.example.rachapro.ui.components.RachaErrorMessage
import com.example.rachapro.ui.components.RachaPrimaryButton
import com.example.rachapro.ui.components.RachaSoftGradient
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onBack: () -> Unit,
    onRegister: () -> Unit,
    onLogin: (
        email: String,
        password: String
    ) -> Unit,
    onLoginSuccess: () -> Unit
) {

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var showPassword by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.LoginSuccess) {
            onLoginSuccess()
        }
    }

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
                .height(240.dp)
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

            Spacer(modifier = Modifier.height(28.dp))

            RachaBrandMark(size = 64.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "RachaPro",
                style = MaterialTheme.typography.headlineLarge,
                color = RachaIndigo
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Bienvenido de nuevo",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Continúa construyendo tu racha académica.",
                style = MaterialTheme.typography.bodyMedium,
                color = RachaOnSurfaceMuted,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            RachaCard {
                Column {
                    FieldLabel(text = "Correo electrónico")

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = {
                            Text(
                                text = "correo@universidad.edu.co",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        shape = MaterialTheme.shapes.small
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FieldLabel(text = "Contraseña")

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = {
                            Text(
                                text = "Ingresa tu contraseña",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        visualTransformation =
                            if (showPassword) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                        trailingIcon = {
                            TextButton(
                                onClick = { showPassword = !showPassword }
                            ) {
                                Text(
                                    text = if (showPassword) "Ocultar" else "Ver",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        },
                        shape = MaterialTheme.shapes.small
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    RachaPrimaryButton(
                        text = if (uiState is AuthUiState.Loading) {
                            "Iniciando sesión..."
                        } else {
                            "Iniciar sesión"
                        },
                        onClick = { onLogin(email, password) },
                        enabled = email.isNotBlank() &&
                            password.isNotBlank() &&
                            uiState !is AuthUiState.Loading
                    )

                    when (uiState) {

                        AuthUiState.InvalidCredentials -> {
                            Spacer(modifier = Modifier.height(12.dp))
                            RachaErrorMessage(message = "Correo o contraseña incorrectos.")
                        }

                        is AuthUiState.ValidationError -> {
                            Spacer(modifier = Modifier.height(12.dp))
                            RachaErrorMessage(message = uiState.message)
                        }

                        AuthUiState.Error -> {
                            Spacer(modifier = Modifier.height(12.dp))
                            RachaErrorMessage(message = "Ocurrió un error. Inténtalo nuevamente.")
                        }

                        else -> Unit
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes cuenta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RachaOnSurfaceMuted
                )
                TextButton(onClick = onRegister) {
                    Text(
                        text = "Crear cuenta",
                        style = MaterialTheme.typography.labelLarge,
                        color = RachaIndigo
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu información académica, en un solo lugar.",
                style = MaterialTheme.typography.labelSmall,
                color = RachaOnSurfaceMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
}
