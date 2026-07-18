package com.example.attendanceapp.features.reports.data

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceHistoryRecord(
    val id: Int,
    val studentId: Int,
    val tipo: String,
    val fecha: String? = null,
    val registeredBy: Int? = null
)

@Serializable
data class StudentStatistics(
    val studentId: Int,
    val studentName: String,
    val totalDays: Int = 0,
    val daysPresent: Int = 0,
    val daysAbsent: Int = 0,
    val tardies: Int = 0,
    val attendanceRate: Double = 0.0
)
