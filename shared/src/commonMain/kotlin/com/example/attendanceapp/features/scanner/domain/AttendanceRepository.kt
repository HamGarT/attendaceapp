package com.example.attendanceapp.features.scanner.domain

import com.example.attendanceapp.features.scanner.data.AttendanceResponse

interface AttendanceRepository {
    suspend fun registerAttendance(studentId: Int): Result<AttendanceResponse>
}

