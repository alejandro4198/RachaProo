package com.example.rachapro.backend.shared.error

import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger =
        LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        exception: IllegalArgumentException
    ): ResponseEntity<ApiError> {
        val error = ApiError(
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = exception.message ?: "Solicitud invalida"
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        exception: MethodArgumentNotValidException
    ): ResponseEntity<ApiError> {
        val fieldErrors =
            exception.bindingResult.fieldErrors
                .sortedBy { it.field }
                .joinToString("; ") { fieldError ->
                    val field = fieldError.field
                    val reason =
                        fieldError.defaultMessage
                            ?: "valor invalido"

                    "$field: $reason"
                }

        val message =
            fieldErrors.ifBlank {
                exception.bindingResult.globalErrors
                    .firstOrNull()
                    ?.defaultMessage
                    ?: "Solicitud invalida"
            }

        val error = ApiError(
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = message
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(
        exception: ConflictException
    ): ResponseEntity<ApiError> {
        val error = ApiError(
            status = HttpStatus.CONFLICT.value(),
            error = HttpStatus.CONFLICT.reasonPhrase,
            message = exception.message ?: "Conflicto"
        )

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(
        exception: DataIntegrityViolationException
    ): ResponseEntity<ApiError> {
        val knownConstraint =
            KnownUniqueConstraint.from(exception)

        if (knownConstraint != null) {
            val error = ApiError(
                status = HttpStatus.CONFLICT.value(),
                error = HttpStatus.CONFLICT.reasonPhrase,
                message = knownConstraint.message
            )

            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error)
        }

        // Una violación de integridad no clasificada no es necesariamente un
        // conflicto del usuario (por ejemplo, un NOT NULL roto por un bug).
        // Se registra como error de servidor en lugar de ocultarla como 409.
        logger.error(
            "Violacion de integridad no clasificada como conflicto de unicidad",
            exception
        )

        val error = ApiError(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "Error interno del servidor"
        )

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error)
    }
}
