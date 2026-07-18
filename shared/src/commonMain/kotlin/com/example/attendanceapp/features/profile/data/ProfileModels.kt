package com.example.attendanceapp.features.profile.data

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: Int,
    val name: String,
    val lastname: String,
    val email: String,
    val emailVerifiedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class ProfileChild(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val sectionId: Int? = null
)
