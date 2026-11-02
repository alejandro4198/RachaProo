package com.example.rachapro.backend.identity.user

import com.example.rachapro.backend.identity.user.dto.CreateUserRequest
import com.example.rachapro.backend.identity.user.dto.UserResponse
import com.example.rachapro.backend.shared.error.ConflictException
import com.example.rachapro.backend.shared.error.GlobalExceptionHandler
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.sql.SQLException

class UserRegistrationValidationTest {

    private val userService: UserService =
        mock(UserService::class.java)

    private val validRequest =
        CreateUserRequest(
            fullName = "Ana Perez",
            email = "ana@example.com",
            password = "secreto123",
            semester = 5,
            acceptedPrivacyPolicy = true
        )

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(UserController(userService))
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    @Test
    fun `valid registration returns 201`() {
        `when`(userService.create(validRequest))
            .thenReturn(userResponse())

        mockMvc.perform(register(body()))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.email").value("ana@example.com"))
    }

    @Test
    fun `blank full name returns 400`() {
        assertInvalid(
            body(fullName = "   "),
            "fullName"
        )
    }

    @Test
    fun `invalid email returns 400`() {
        assertInvalid(
            body(email = "ana-arroba-example.com"),
            "email"
        )
    }

    @Test
    fun `password shorter than 8 characters returns 400`() {
        assertInvalid(
            body(password = "corto12"),
            "password"
        )
    }

    @Test
    fun `semester 0 returns 400`() {
        assertInvalid(
            body(semester = 0),
            "semester"
        )
    }

    @Test
    fun `semester 11 returns 400`() {
        assertInvalid(
            body(semester = 11),
            "semester"
        )
    }

    @Test
    fun `privacy policy not accepted returns 400`() {
        assertInvalid(
            body(acceptedPrivacyPolicy = false),
            "acceptedPrivacyPolicy"
        )
    }

    @Test
    fun `duplicate email returns 409`() {
        `when`(userService.create(validRequest))
            .thenThrow(
                ConflictException("El correo ya está registrado")
            )

        mockMvc.perform(register(body()))
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(
                jsonPath("$.message")
                    .value("El correo ya está registrado")
            )
    }

    @Test
    fun `unique violation on users email returns 409`() {
        `when`(userService.create(validRequest))
            .thenThrow(
                integrityViolation(
                    "duplicate key value violates unique constraint \"users_email_key\""
                )
            )

        mockMvc.perform(register(body()))
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(
                jsonPath("$.message")
                    .value("El correo ya está registrado")
            )
    }

    @Test
    fun `unique violation on achievements returns 409`() {
        `when`(userService.create(validRequest))
            .thenThrow(
                integrityViolation(
                    "duplicate key value violates unique constraint \"uq_achievements_user_type\""
                )
            )

        mockMvc.perform(register(body()))
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(
                jsonPath("$.message")
                    .value("El logro ya está registrado para este usuario")
            )
    }

    @Test
    fun `unique violation on categories returns 409`() {
        `when`(userService.create(validRequest))
            .thenThrow(
                integrityViolation(
                    "duplicate key value violates unique constraint \"uq_categories_user_name\""
                )
            )

        mockMvc.perform(register(body()))
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(
                jsonPath("$.message")
                    .value("La categoría ya existe para este usuario")
            )
    }

    @Test
    fun `unclassified integrity violation is not reported as conflict`() {
        `when`(userService.create(validRequest))
            .thenThrow(
                integrityViolation(
                    "null value in column \"email\" of relation \"users\" violates not-null constraint"
                )
            )

        mockMvc.perform(register(body()))
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.status").value(500))
    }

    private fun assertInvalid(
        body: String,
        field: String
    ) {
        mockMvc.perform(register(body))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(
                jsonPath("$.message")
                    .value(containsString(field))
            )

        verifyNoInteractions(userService)
    }

    private fun register(body: String) =
        post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)

    private fun body(
        fullName: String = validRequest.fullName,
        email: String = validRequest.email,
        password: String = validRequest.password,
        semester: Int = validRequest.semester,
        acceptedPrivacyPolicy: Boolean =
            validRequest.acceptedPrivacyPolicy
    ): String =
        """
        {
          "fullName": "$fullName",
          "email": "$email",
          "password": "$password",
          "semester": $semester,
          "acceptedPrivacyPolicy": $acceptedPrivacyPolicy
        }
        """.trimIndent()

    private fun userResponse() =
        UserResponse(
            id = 1L,
            fullName = validRequest.fullName,
            email = validRequest.email,
            semester = validRequest.semester,
            acceptedPrivacyPolicy =
                validRequest.acceptedPrivacyPolicy,
            createdAt = 1000L,
            updatedAt = 1000L
        )

    private fun integrityViolation(message: String) =
        DataIntegrityViolationException(
            "could not execute statement",
            SQLException(message)
        )
}
