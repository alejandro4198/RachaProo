package com.example.rachapro.backend.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.util.Base64
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtConfig(
    @Value("\${RACHAPRO_JWT_SECRET}")
    private val jwtSecretBase64: String
) {

    @Bean
    fun jwtSecretKey(): SecretKey {
        val decoded = Base64.getDecoder().decode(jwtSecretBase64)

        require(decoded.size >= 32) {
            "RACHAPRO_JWT_SECRET debe contener al menos 32 bytes"
        }

        return SecretKeySpec(
            decoded,
            "HmacSHA256"
        )
    }

    @Bean
    fun jwtEncoder(
        secretKey: SecretKey
    ): JwtEncoder =
        NimbusJwtEncoder
            .withSecretKey(secretKey)
            .algorithm(MacAlgorithm.HS256)
            .build()

    @Bean
    fun jwtDecoder(
        secretKey: SecretKey
    ): JwtDecoder =
        NimbusJwtDecoder
            .withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()
}