package com.example.rachapro.backend.security

import com.example.rachapro.backend.user.UserEntity
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class JwtService(
    private val jwtEncoder: JwtEncoder
) {

    fun generateToken(user: UserEntity): String {
        val now = Instant.now()

        val claims = JwtClaimsSet.builder()
            .issuer("rachapro-backend")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(3600))
            .subject(user.id.toString())
            .claim("email", user.email)
            .build()

        val header = JwsHeader
            .with(MacAlgorithm.HS256)
            .type("JWT")
            .build()

        return jwtEncoder.encode(
            JwtEncoderParameters.from(
                header,
                claims
            )
        ).tokenValue
    }
}