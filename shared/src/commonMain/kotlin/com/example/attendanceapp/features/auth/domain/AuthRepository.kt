package com.example.attendanceapp.features.auth.domain

interface AuthRepository {
    suspend fun login(email: String, password: String, rememberMe: Boolean = true): Result<User>
}
