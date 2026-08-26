package com.example.rachapro.backend.auth

import com.example.rachapro.backend.auth.dto.LoginRequest
import com.example.rachapro.backend.user.PasswordHasher
import com.example.rachapro.backend.user.UserRepository
import com.example.rachapro.backend.user.dto.UserResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher
) {

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): UserResponse? {
        val email = request.email.trim().lowercase()

        val user = userRepository.findByEmail(email)
            ?: return null

        val validPassword = passwordHasher.verify(
            password = request.password,
            saltBase64 = user.passwordSalt,
            expectedHashBase64 = user.passwordHash
        )

        if (!validPassword) {
            return null
        }

        return UserResponse(
            id = user.id,
            fullName = user.fullName,
            email = user.email,
            semester = user.semester,
            acceptedPrivacyPolicy = user.acceptedPrivacyPolicy,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}