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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect

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

    val purple = Color(0xFF6847FF)
    val blue = Color(0xFF5280FF)
    val background = Color(0xFFF8F9FD)
    val primaryText = Color(0xFF171B26)
    val secondaryText = Color(0xFF7A8194)

    val gradient = Brush.horizontalGradient(
        colors = listOf(
            purple,
            blue
        )
    )

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
                modifier = Modifier.height(22.dp)
            )

            Text(
                text = "Crear cuenta",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                color = primaryText
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Comienza tu racha académica",
                fontSize = 16.sp,
                color = secondaryText
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            FieldLabel(
                text = "Nombre completo",
                required = true
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                },
                placeholder = {
                    Text("Juan Pérez")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            FieldLabel(
                text = "Correo electrónico",
                required = true
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
                modifier = Modifier.height(16.dp)
            )

            FieldLabel(
                text = "Contraseña",
                required = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                placeholder = {
                    Text("Mínimo 8 caracteres")
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
                            text = if (showPassword) {
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
                modifier = Modifier.height(16.dp)
            )

            FieldLabel(
                text = "Confirmar contraseña",
                required = true
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                },
                placeholder = {
                    Text("Repite tu contraseña")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation =
                    if (showConfirmPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                trailingIcon = {
                    TextButton(
                        onClick = {
                            showConfirmPassword =
                                !showConfirmPassword
                        }
                    ) {
                        Text(
                            text =
                                if (showConfirmPassword) {
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
                modifier = Modifier.height(16.dp)
            )

            FieldLabel(
                text = "Semestre",
                required = true
            )

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedButton(
                    onClick = {
                        semesterMenuExpanded = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Text(
                        text =
                            if (semester.isEmpty()) {
                                "Selecciona tu semestre"
                            } else {
                                semester
                            },
                        modifier = Modifier.weight(1f),
                        color =
                            if (semester.isEmpty()) {
                                secondaryText
                            } else {
                                primaryText
                            }
                    )

                    Text(
                        text = "⌄",
                        color = primaryText
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

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
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
                    modifier = Modifier.padding(
                        top = 12.dp
                    ),
                    color = secondaryText,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Button(
                onClick = {

                    val semesterNumber =
                        semester
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
                enabled =
                    fullName.isNotBlank() &&
                            email.isNotBlank() &&
                            password.length >= 8 &&
                            password == confirmPassword &&
                            semester.isNotBlank() &&
                            acceptPrivacy &&
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
                            "Creando cuenta..."
                        } else {
                            "Crear cuenta"
                        },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            when (uiState) {

                AuthUiState.EmailAlreadyRegistered -> {

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Ya existe una cuenta registrada con este correo.",
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
                        text = "No fue posible crear la cuenta. Inténtalo nuevamente.",
                        color = Color(0xFFFF4B55),
                        fontSize = 14.sp
                    )
                }

                else -> Unit
            }

            Spacer(
                modifier = Modifier.height(14.dp)

            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "¿Ya tienes cuenta?",
                    color = secondaryText,
                    fontSize = 14.sp
                )

                TextButton(
                    onClick = onLogin
                ) {

                    Text(
                        text = "Iniciar sesión",
                        color = purple,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(
    text: String,
    required: Boolean
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF171B26)
        )

        if (required) {

            Text(
                text = " *",
                color = Color(0xFFFF4B55),
                fontSize = 14.sp
            )
        }
    }

    Spacer(
        modifier = Modifier.height(6.dp)
    )
}