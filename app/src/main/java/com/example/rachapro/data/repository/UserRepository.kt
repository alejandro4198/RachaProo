package com.example.rachapro.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.rachapro.data.local.dao.UserDao
import com.example.rachapro.data.local.entity.UserEntity
import com.example.rachapro.security.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun getUserById(
        userId: Long
    ): UserEntity? {

        return userDao.getUserById(
            userId = userId
        )
    }

    suspend fun registerUser(
        fullName: String,
        email: String,
        password: String,
        semester: Int,
        acceptedPrivacyPolicy: Boolean
    ): RegisterResult {

        val normalizedEmail = email
            .trim()
            .lowercase()

        val normalizedName = fullName.trim()

        if (userDao.emailExists(normalizedEmail)) {
            return RegisterResult.EmailAlreadyRegistered
        }

        val passwordResult = withContext(Dispatchers.Default) {
            PasswordHasher.hashPassword(password)
        }

        val currentTime = System.currentTimeMillis()

        val user = UserEntity(
            fullName = normalizedName,
            email = normalizedEmail,
            passwordHash = passwordResult.hash,
            passwordSalt = passwordResult.salt,
            semester = semester,
            acceptedPrivacyPolicy = acceptedPrivacyPolicy,
            createdAt = currentTime,
            updatedAt = currentTime
        )

        return try {

            val userId = userDao.insertUser(user)

            RegisterResult.Success(
                userId = userId
            )

        } catch (_: SQLiteConstraintException) {

            RegisterResult.EmailAlreadyRegistered

        } catch (_: Exception) {

            RegisterResult.Error
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): LoginResult {

        val normalizedEmail = email
            .trim()
            .lowercase()

        val user = userDao.getUserByEmail(
            normalizedEmail
        ) ?: return LoginResult.InvalidCredentials

        val passwordIsValid = withContext(Dispatchers.Default) {
            PasswordHasher.verifyPassword(
                password = password,
                storedHash = user.passwordHash,
                storedSalt = user.passwordSalt
            )
        }

        return if (passwordIsValid) {

            LoginResult.Success(
                user = user
            )

        } else {

            LoginResult.InvalidCredentials
        }
    }

    suspend fun updateProfile(
        userId: Long,
        fullName: String,
        semester: Int
    ): UpdateProfileResult {

        val normalizedName = fullName.trim()

        if (normalizedName.isBlank()) {
            return UpdateProfileResult.InvalidData(
                "El nombre no puede estar vacío."
            )
        }

        if (semester !in 1..10) {
            return UpdateProfileResult.InvalidData(
                "Selecciona un semestre válido."
            )
        }

        val user =
            userDao.getUserById(userId = userId)
                ?: return UpdateProfileResult.NotFound

        return try {

            userDao.updateUser(
                user.copy(
                    fullName = normalizedName,
                    semester = semester,
                    updatedAt = System.currentTimeMillis()
                )
            )

            UpdateProfileResult.Success

        } catch (_: Exception) {

            UpdateProfileResult.Error
        }
    }
}

sealed interface RegisterResult {

    data class Success(
        val userId: Long
    ) : RegisterResult

    data object EmailAlreadyRegistered :
        RegisterResult

    data object Error :
        RegisterResult
}

sealed interface LoginResult {

    data class Success(
        val user: UserEntity
    ) : LoginResult

    data object InvalidCredentials :
        LoginResult
}

sealed interface UpdateProfileResult {

    data object Success : UpdateProfileResult

    data object NotFound : UpdateProfileResult

    data class InvalidData(
        val message: String
    ) : UpdateProfileResult

    data object Error : UpdateProfileResult
}