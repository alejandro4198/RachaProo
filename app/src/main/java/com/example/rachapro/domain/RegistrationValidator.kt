package com.example.rachapro.domain

object RegistrationValidator {

    fun validate(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        semester: Int?,
        acceptedPrivacyPolicy: Boolean
    ): String? {

        if (fullName.isBlank()) {
            return "El nombre completo es obligatorio."
        }

        if (!isEmailValid(email)) {
            return "Ingresa un correo electrónico válido."
        }

        if (password.length < 8) {
            return "La contraseña debe tener mínimo 8 caracteres."
        }

        if (password != confirmPassword) {
            return "Las contraseñas no coinciden."
        }

        if (semester == null || semester !in 1..10) {
            return "Selecciona un semestre válido."
        }

        if (!acceptedPrivacyPolicy) {
            return "Debes aceptar la política de privacidad."
        }

        return null
    }

    fun isEmailValid(
        email: String
    ): Boolean {

        val emailRegex =
            Regex(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            )

        return emailRegex.matches(
            email.trim()
        )
    }
}
