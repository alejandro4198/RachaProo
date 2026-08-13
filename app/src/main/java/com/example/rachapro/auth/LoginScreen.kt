package com.example.rachapro.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect

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

    val purple = Color(0xFF6847FF)
    val background = Color(0xFFF8F9FD)
    val primaryText = Color(0xFF171B26)
    val secondaryText = Color(0xFF7A8194)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 30.dp)
        ) {

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp)
            ) {

                Text(
                    text = "‹",
                    fontSize = 24.sp,
                    color = secondaryText
                )
            }

            Spacer(
                modifier = Modifier.height(40.dp)
            )

            Text(
                text = "Bienvenido de nuevo",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                color = primaryText
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Continúa construyendo tu racha",
                fontSize = 16.sp,
                color = secondaryText
            )

            Spacer(
                modifier = Modifier.height(38.dp)
            )

            Text(
                text = "Correo electrónico *",
                color = primaryText,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                placeholder = {
                    Text("correo@universidad.edu.co")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Contraseña *",
                color = primaryText,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                placeholder = {
                    Text("Ingresa tu contraseña")
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
                        onClick = {
                            showPassword = !showPassword
                        }
                    ) {

                        Text(
                            text =
                                if (showPassword) {
                                    "Ocultar"
                                } else {
                                    "Ver"
                                }
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Button(
                onClick = {
                    onLogin(
                        email,
                        password
                    )
                },
                enabled =
                    email.isNotBlank() &&
                            password.isNotBlank() &&
                            uiState !is AuthUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple,
                    contentColor = Color.White
                )
            ) {

                Text(
                    text =
                        if (uiState is AuthUiState.Loading) {
                            "Iniciando sesión..."
                        } else {
                            "Iniciar sesión"
                        },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            when (uiState) {

                AuthUiState.InvalidCredentials -> {

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Correo o contraseña incorrectos.",
                        color = Color(0xFFFF4B55),
                        fontSize = 14.sp
                    )
                }

                is AuthUiState.ValidationError -> {

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = uiState.message,
                        color = Color(0xFFFF4B55),
                        fontSize = 14.sp
                    )
                }

                AuthUiState.Error -> {

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Ocurrió un error. Inténtalo nuevamente.",
                        color = Color(0xFFFF4B55),
                        fontSize = 14.sp
                    )
                }

                else -> Unit
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            TextButton(
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "¿No tienes cuenta? Crear cuenta",
                    color = purple,
                    fontSize = 14.sp
                )
            }
        }
    }
}