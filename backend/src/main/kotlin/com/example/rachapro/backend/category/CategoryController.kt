package com.example.rachapro.backend.category

import com.example.rachapro.backend.category.dto.CategoryResponse
import com.example.rachapro.backend.category.dto.CreateCategoryRequest
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
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @GetMapping
    fun findAll(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<CategoryResponse>> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        return ResponseEntity.ok(
            categoryService.findActiveByUserId(userId)
        )
    }

    @PostMapping
    fun create(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: CreateCategoryRequest
    ): ResponseEntity<CategoryResponse> {
        val userId = jwt.subject
            ?.toLongOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build()

        val category = categoryService.create(userId, request)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(category)
    }
}