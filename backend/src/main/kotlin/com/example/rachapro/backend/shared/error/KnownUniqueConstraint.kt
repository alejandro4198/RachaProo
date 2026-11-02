package com.example.rachapro.backend.shared.error

/**
 * Constraints UNIQUE demostrados en el esquema (`infra/postgres/init/001-schema.sql`).
 *
 * La base de datos es la última barrera de unicidad. Cuando una carrera concurrente
 * supera el pre-check de la aplicación, el driver reporta el nombre del constraint
 * violado dentro de la cadena de causas. Solo esos nombres se traducen a HTTP 409;
 * cualquier otra violación de integridad conserva el comportamiento de error 500.
 *
 * Se aceptan tanto los nombres explícitos del esquema como los nombres por defecto
 * que genera el DDL automático, para no depender de una única forma de creación.
 */
internal enum class KnownUniqueConstraint(
    val message: String,
    private val identifiers: Set<String>
) {
    USER_EMAIL(
        message = "El correo ya está registrado",
        identifiers = setOf(
            "users_email_key",
            "users.email",
            "uq_users_email"
        )
    ),
    ACHIEVEMENT(
        message = "El logro ya está registrado para este usuario",
        identifiers = setOf(
            "uq_achievements_user_type",
            "achievements_user_id_type_key"
        )
    ),
    CATEGORY(
        message = "La categoría ya existe para este usuario",
        identifiers = setOf(
            "uq_categories_user_name",
            "categories_user_id_name_key"
        )
    );

    companion object {

        fun from(exception: Throwable): KnownUniqueConstraint? {
            var current: Throwable? = exception
            val visited = mutableSetOf<Throwable>()

            while (current != null && visited.add(current)) {
                val text = current.message?.lowercase()

                if (!text.isNullOrBlank()) {
                    entries.firstOrNull { constraint ->
                        constraint.identifiers.any(text::contains)
                    }?.let { return it }
                }

                current = current.cause
            }

            return null
        }
    }
}
