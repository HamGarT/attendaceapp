package com.example.attendanceapp.features.scanner.data

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRequest(
    val studentId: Int,
    val tipo: String = "INGRESO",
    val observacion: String = "Llegó a tiempo"
)


@Serializable
data class StudentData(
    val nombres: String,
    val apellidos: String,
    val dni: String
)

@Serializable
data class AttendanceResponse(
    val id: Int? = null,
    val tipo: String? = null,
    val fecha: String? = null,
    val student: StudentData? = null,
    val error: String? = null,
    val message: String? = null
)