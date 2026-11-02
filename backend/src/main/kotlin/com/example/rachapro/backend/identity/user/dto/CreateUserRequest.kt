package com.example.rachapro.backend.identity.user.dto

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateUserRequest(

    @field:NotBlank(message = "El nombre es obligatorio")
    val fullName: String,

    @field:NotBlank(message = "El correo es obligatorio")
    @field:Email(message = "El correo no es válido")
    val email: String,

    @field:NotBlank(message = "La contraseña es obligatoria")
    @field:Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    val password: String,

    @field:Min(value = 1, message = "El semestre debe estar entre 1 y 10")
    @field:Max(value = 10, message = "El semestre debe estar entre 1 y 10")
    val semester: Int,

    @field:AssertTrue(message = "Debe aceptar la política de privacidad")
    val acceptedPrivacyPolicy: Boolean
)
