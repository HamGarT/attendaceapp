package com.example.attendanceapp.features.auth.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false
)

@Serializable
data class LoginResponse(
    val token: String? = null,
    val user: UserInfo? = null,
    @JsonNames("rememberToken", "refreshToken", "refresh_token", "remember_token")
    val refreshToken: String? = null
)

@Serializable
data class UserInfo(
    val id: Int? = null,
    val name: String? = null,
    val lastname: String? = null,
    val email: String? = null,
    val roles: List<String> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null
)
