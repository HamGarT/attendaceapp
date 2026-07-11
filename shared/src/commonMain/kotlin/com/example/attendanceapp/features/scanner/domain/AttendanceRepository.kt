package com.example.attendanceapp.features.scanner.domain

interface AttendanceRepository {
    suspend fun registerAttendance(studentId: Int): Result<String>
}

