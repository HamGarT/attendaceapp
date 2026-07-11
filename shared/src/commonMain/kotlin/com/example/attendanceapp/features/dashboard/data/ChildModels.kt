package com.example.attendanceapp.features.dashboard.data

import kotlinx.serialization.Serializable

@Serializable
data class ParentChild(
    val parentesco: String,
    val student: Student
)

@Serializable
data class Student(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val sectionId: Int? = null
)
