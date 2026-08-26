package com.example.rachapro.backend.user

import com.example.rachapro.backend.user.dto.CreateUserRequest
import com.example.rachapro.backend.user.dto.UserResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}