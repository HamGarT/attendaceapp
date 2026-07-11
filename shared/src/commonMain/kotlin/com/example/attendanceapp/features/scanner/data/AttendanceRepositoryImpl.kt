package com.example.attendanceapp.features.scanner.data

import com.example.attendanceapp.features.scanner.domain.AttendanceRepository

class AttendanceRepositoryImpl(
    private val attendanceService: AttendanceService = AttendanceService()
) : AttendanceRepository {

    override suspend fun registerAttendance(studentId: Int): Result<String> {
        return try {
            val response = attendanceService.registerAttendance(studentId)
            if (response.success == true || response.message != null) {
                Result.success(response.message ?: "Asistencia registrada correctamente")
            } else {
                Result.failure(Exception(response.error ?: "Error desconocido al registrar asistencia"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

