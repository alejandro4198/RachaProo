package com.example.rachapro.backend.user

import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Component
class PasswordHasher {

    private val iterations = 210_000
    private val keyLength = 256
    private val saltLength = 16

    fun generateSalt(): String {
        val salt = ByteArray(saltLength)
        SecureRandom().nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hash(password: String, saltBase64: String): String {
        val salt = Base64.getDecoder().decode(saltBase64)

        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            iterations,
            keyLength
        )

        val factory = SecretKeyFactory.getInstance(
            "PBKDF2WithHmacSHA1"
        )

        val hash = factory.generateSecret(spec).encoded

        spec.clearPassword()

        return Base64.getEncoder().encodeToString(hash)
    }

    fun verify(
        password: String,
        saltBase64: String,
        expectedHashBase64: String
    ): Boolean {
        val calculatedHash = Base64.getDecoder().decode(
            hash(password, saltBase64)
        )

        val expectedHash = Base64.getDecoder().decode(
            expectedHashBase64
        )

        return MessageDigest.isEqual(
            calculatedHash,
            expectedHash
        )
    }
}