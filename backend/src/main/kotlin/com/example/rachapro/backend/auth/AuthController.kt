package com.example.rachapro.backend.auth

import com.example.rachapro.backend.auth.dto.LoginRequest
import com.example.rachapro.backend.auth.dto.LoginResponse
import com.example.rachapro.backend.user.UserService
import com.example.rachapro.backend.user.dto.UserResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val userService: UserService
) {

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest
    ): ResponseEntity<LoginResponse> {
        val response = authService.login(request)
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        return ResponseEntity.ok(response)
    }

    @GetMapping("/me")
    fun me(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<UserResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val user = userService.findById(userId)
            ?: return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build()

        return ResponseEntity.ok(user)
    }
}