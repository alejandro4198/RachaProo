package com.example.rachapro.backend.user

import com.example.rachapro.backend.user.dto.CreateUserRequest
import com.example.rachapro.backend.user.dto.UserResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher
) {

    @Transactional
    fun create(request: CreateUserRequest): UserResponse {
        val email = request.email.trim().lowercase()

        require(!userRepository.existsByEmail(email)) {
            "El correo ya está registrado"
        }

        val salt = passwordHasher.generateSalt()
        val hash = passwordHasher.hash(
            request.password,
            salt
        )

        val now = System.currentTimeMillis()

        val user = UserEntity(
            fullName = request.fullName.trim(),
            email = email,
            passwordHash = hash,
            passwordSalt = salt,
            semester = request.semester,
            acceptedPrivacyPolicy = request.acceptedPrivacyPolicy,
            createdAt = now,
            updatedAt = now
        )

        return userRepository.save(user).toResponse()
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): UserResponse? =
        userRepository.findById(id)
            .orElse(null)
            ?.toResponse()

    @Transactional(readOnly = true)
    fun findByEmail(email: String): UserResponse? =
        userRepository.findByEmail(
            email.trim().lowercase()
        )?.toResponse()

    private fun UserEntity.toResponse() =
        UserResponse(
            id = id,
            fullName = fullName,
            email = email,
            semester = semester,
            acceptedPrivacyPolicy = acceptedPrivacyPolicy,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}