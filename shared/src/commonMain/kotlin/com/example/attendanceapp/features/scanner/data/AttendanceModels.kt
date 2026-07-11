package com.example.attendanceapp.features.scanner.data

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRequest(
    val studentId: Int,
    val tipo: String = "INGRESO",
    val observacion: String = "Llegó a tiempo"
)

// ✨ AHORA ATRAPAMOS LOS DATOS DEL ALUMNO
@Serializable
data class StudentData(
    val nombres: String,
    val apellidos: String,
    val dni: String
)

@Serializable
data class AttendanceResponse(
    val id: Int? = null,
    val tipo: String? = null,           // "INGRESO" o "SALIDA"
    val fecha: String? = null,          // Hora de registro
    val student: StudentData? = null,   // Los datos del alumno
    val error: String? = null,
    val message: String? = null
)