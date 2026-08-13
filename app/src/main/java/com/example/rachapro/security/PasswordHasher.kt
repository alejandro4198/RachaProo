package com.example.rachapro.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {

    private const val ALGORITHM = "PBKDF2WithHmacSHA1"

    private const val ITERATIONS = 210_000

    private const val KEY_LENGTH = 256

    private const val SALT_LENGTH = 16

    data class HashResult(
        val hash: String,
        val salt: String
    )

    fun hashPassword(
        password: String
    ): HashResult {

        val saltBytes = ByteArray(SALT_LENGTH)

        SecureRandom().nextBytes(saltBytes)

        val hashBytes = generateHash(
            password = password,
            salt = saltBytes
        )

        return HashResult(
            hash = Base64.encodeToString(
                hashBytes,
                Base64.NO_WRAP
            ),
            salt = Base64.encodeToString(
                saltBytes,
                Base64.NO_WRAP
            )
        )
    }

    fun verifyPassword(
        password: String,
        storedHash: String,
        storedSalt: String
    ): Boolean {

        val saltBytes = Base64.decode(
            storedSalt,
            Base64.NO_WRAP
        )

        val expectedHash = Base64.decode(
            storedHash,
            Base64.NO_WRAP
        )

        val calculatedHash = generateHash(
            password = password,
            salt = saltBytes
        )

        return MessageDigest.isEqual(
            expectedHash,
            calculatedHash
        )
    }

    private fun generateHash(
        password: String,
        salt: ByteArray
    ): ByteArray {

        val specification = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )

        return try {

            val factory = SecretKeyFactory.getInstance(
                ALGORITHM
            )

            factory
                .generateSecret(specification)
                .encoded

        } finally {

            specification.clearPassword()
        }
    }
}