package com.example.attendanceapp.features.reports.domain

import com.example.attendanceapp.features.reports.data.AttendanceHistoryRecord
import com.example.attendanceapp.features.reports.data.ParentChildReport

interface ReportsRepository {
    suspend fun getChildren(): Result<List<ParentChildReport>>
    suspend fun getAttendanceHistory(studentId: Int): Result<List<AttendanceHistoryRecord>>
}