package com.example.attendanceapp.features.scanner.data

import com.example.attendanceapp.features.scanner.domain.AttendanceRepository

class AttendanceRepositoryImpl(
    private val attendanceService: AttendanceService = AttendanceService()
) : AttendanceRepository {

    override suspend fun registerAttendance(studentId: Int): Result<AttendanceResponse> {
        return try {
            val response = attendanceService.registerAttendance(studentId)

            if (response.id != null) {
                // ✨ Devolvemos TODA la respuesta con los datos
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ?: response.error ?: "Error desconocido al registrar asistencia"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

