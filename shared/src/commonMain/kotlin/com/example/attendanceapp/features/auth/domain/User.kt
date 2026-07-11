package com.example.attendanceapp.features.auth.domain

data class User(
    val id: Int,
    val name: String,
    val lastname: String,
    val email: String,
    val role: String,
    val token: String = ""
)
