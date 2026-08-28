package com.example.rachapro.backend.user

import com.example.rachapro.backend.category.CategoryEntity
import com.example.rachapro.backend.category.CategoryRepository
import com.example.rachapro.backend.error.ConflictException
import com.example.rachapro.backend.user.dto.CreateUserRequest
import com.example.rachapro.backend.user.dto.UserResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.example.rachapro.backend.user.dto.UpdateUserRequest

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val categoryRepository: CategoryRepository
) {

    @Transactional
    fun create(request: CreateUserRequest): UserResponse {
        val email = request.email.trim().lowercase()

        if (userRepository.existsByEmail(email)) {
            throw ConflictException(
                "El correo ya está registrado"
            )
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

        val savedUser =
            userRepository.save(user)

        val defaultCategories =
            listOf(
                CategoryEntity(
                    userId = savedUser.id,
                    name = "Universidad",
                    createdAt = now,
                    updatedAt = now,
                    isActive = true
                ),
                CategoryEntity(
                    userId = savedUser.id,
                    name = "Personal",
                    createdAt = now,
                    updatedAt = now,
                    isActive = true
                ),
                CategoryEntity(
                    userId = savedUser.id,
                    name = "Trabajo",
                    createdAt = now,
                    updatedAt = now,
                    isActive = true
                )
            )

        categoryRepository.saveAll(
            defaultCategories
        )

        return savedUser.toResponse()
    }

    @Transactional
    fun updateProfile(
        id: Long,
        request: UpdateUserRequest
    ): UserResponse? {

        val normalizedName =
            request.fullName.trim()

        require(normalizedName.isNotBlank()) {
            "El nombre no puede estar vacío"
        }

        require(request.semester in 1..10) {
            "El semestre no es válido"
        }

        val user =
            userRepository.findById(id)
                .orElse(null)
                ?: return null

        user.fullName = normalizedName
        user.semester = request.semester
        user.updatedAt = System.currentTimeMillis()

        return userRepository
            .save(user)
            .toResponse()
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