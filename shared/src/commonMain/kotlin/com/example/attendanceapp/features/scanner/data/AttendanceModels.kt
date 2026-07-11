package com.example.attendanceapp.features.scanner.data

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRequest(
    val studentId: Int,
    val tipo: String = "INGRESO",
    val observacion: String = "Llegó a tiempo"
)

@Serializable
data class AttendanceResponse(
    val id: Int? = null,
    val message: String? = null,
    val success: Boolean? = null,
    val error: String? = null
)

