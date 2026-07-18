package com.example.attendanceapp.features.reports.data

import com.example.attendanceapp.features.reports.domain.ReportsRepository

class ReportsRepositoryImpl(
    private val reportsService: ReportsService = ReportsService()
) : ReportsRepository {

    override suspend fun getChildren(): Result<List<ParentChildReport>> {
        return try {
            val children = reportsService.getChildren()
            Result.success(children)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAttendanceHistory(studentId: Int): Result<List<AttendanceHistoryRecord>> {
        return try {
            val history = reportsService.getAttendanceHistory(studentId)
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
