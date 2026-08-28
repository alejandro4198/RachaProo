package com.example.rachapro.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.rachapro.data.local.dao.UserDao
import com.example.rachapro.data.local.entity.UserEntity
import com.example.rachapro.network.ApiService
import com.example.rachapro.network.dto.LoginRequest
import com.example.rachapro.network.dto.UserResponse
import com.example.rachapro.security.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import com.example.rachapro.network.dto.CreateUserRequest
import com.example.rachapro.network.dto.UpdateUserRequest

class UserRepository(
    private val userDao: UserDao,
    private val apiService: ApiService
) {

    suspend fun getUserById(
        userId: Long
    ): UserEntity? {

        return try {

            val response =
                apiService.getCurrentUser()

            val existing =
                userDao.getUserById(
                    userId = response.id
                )

            val user =
                UserEntity(
                    id = response.id,
                    fullName = response.fullName,
                    email = response.email,
                    passwordHash =
                        existing?.passwordHash ?: "",
                    passwordSalt =
                        existing?.passwordSalt ?: "",
                    semester = response.semester,
                    acceptedPrivacyPolicy =
                        response.acceptedPrivacyPolicy,
                    createdAt = response.createdAt,
                    updatedAt = response.updatedAt
                )

            userDao.upsertUser(user)

            user

        } catch (_: Exception) {

            userDao.getUserById(
                userId = userId
            )
        }
    }

    suspend fun registerUser(
        fullName: String,
        email: String,
        password: String,
        semester: Int,
        acceptedPrivacyPolicy: Boolean
    ): RegisterResult {

        val normalizedEmail =
            email.trim().lowercase()

        val normalizedName =
            fullName.trim()

        return try {

            val response =
                apiService.createUser(
                    CreateUserRequest(
                        fullName = normalizedName,
                        email = normalizedEmail,
                        password = password,
                        semester = semester,
                        acceptedPrivacyPolicy =
                            acceptedPrivacyPolicy
                    )
                )

            val passwordResult =
                withContext(Dispatchers.Default) {
                    PasswordHasher.hashPassword(password)
                }

            userDao.upsertUser(
                UserEntity(
                    id = response.id,
                    fullName = response.fullName,
                    email = response.email,
                    passwordHash = passwordResult.hash,
                    passwordSalt = passwordResult.salt,
                    semester = response.semester,
                    acceptedPrivacyPolicy =
                        response.acceptedPrivacyPolicy,
                    createdAt = response.createdAt,
                    updatedAt = response.updatedAt
                )
            )

            RegisterResult.Success(
                userId = response.id
            )

        } catch (exception: HttpException) {

            if (exception.code() == 409) {
                RegisterResult.EmailAlreadyRegistered
            } else {
                RegisterResult.Error
            }

        } catch (_: IOException) {

            RegisterResult.Error

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

        return try {

            val response = apiService.login(
                LoginRequest(
                    email = normalizedEmail,
                    password = password
                )
            )

            LoginResult.Success(
                user = response.user,
                token = response.token
            )

        } catch (exception: HttpException) {

            if (exception.code() == 401) {
                LoginResult.InvalidCredentials
            } else {
                LoginResult.Error
            }

        } catch (_: IOException) {

            LoginResult.Error

        } catch (_: Exception) {

            LoginResult.Error
        }
    }

    suspend fun updateProfile(
        userId: Long,
        fullName: String,
        semester: Int
    ): UpdateProfileResult {

        val normalizedName =
            fullName.trim()

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

        return try {

            val response =
                apiService.updateCurrentUser(
                    UpdateUserRequest(
                        fullName = normalizedName,
                        semester = semester
                    )
                )

            val existing =
                userDao.getUserById(
                    userId = response.id
                )

            userDao.upsertUser(
                UserEntity(
                    id = response.id,
                    fullName = response.fullName,
                    email = response.email,
                    passwordHash =
                        existing?.passwordHash ?: "",
                    passwordSalt =
                        existing?.passwordSalt ?: "",
                    semester = response.semester,
                    acceptedPrivacyPolicy =
                        response.acceptedPrivacyPolicy,
                    createdAt = response.createdAt,
                    updatedAt = response.updatedAt
                )
            )

            UpdateProfileResult.Success

        } catch (exception: HttpException) {

            when (exception.code()) {

                400 ->
                    UpdateProfileResult.InvalidData(
                        "Los datos ingresados no son válidos."
                    )

                404 ->
                    UpdateProfileResult.NotFound

                else ->
                    UpdateProfileResult.Error
            }

        } catch (_: IOException) {

            UpdateProfileResult.Error

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
        val user: UserResponse,
        val token: String
    ) : LoginResult

    data object InvalidCredentials :
        LoginResult

    data object Error :
        LoginResult
}

sealed interface UpdateProfileResult {

    data object Success :
        UpdateProfileResult

    data object NotFound :
        UpdateProfileResult

    data class InvalidData(
        val message: String
    ) : UpdateProfileResult

    data object Error :
        UpdateProfileResult
}