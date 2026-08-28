package com.example.rachapro.backend.user

import com.example.rachapro.backend.user.dto.CreateUserRequest
import com.example.rachapro.backend.user.dto.UserResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.example.rachapro.backend.user.dto.UpdateUserRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PatchMapping

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun create(
        @RequestBody request: CreateUserRequest
    ): ResponseEntity<UserResponse> {
        val user = userService.create(request)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(user)
    }

    @PatchMapping("/me")
    fun updateMe(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UserResponse> {

        val userId =
            jwt.subject
                ?.toLongOrNull()
                ?: return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build()

        val user =
            userService.updateProfile(
                id = userId,
                request = request
            )
                ?: return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build()

        return ResponseEntity.ok(user)
    }
}