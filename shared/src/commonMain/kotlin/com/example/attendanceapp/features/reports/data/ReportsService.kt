package com.example.attendanceapp.features.reports.data

import com.example.attendanceapp.core.BuildConfig
import com.example.attendanceapp.core.network.authGet
import com.example.attendanceapp.core.network.createAuthHttpClient
import kotlinx.serialization.Serializable

class ReportsService {

    companion object {
        private const val BASE_URL = BuildConfig.API_URL
    }

    private val client = createAuthHttpClient()

    suspend fun getChildren(): List<ParentChildReport> {
        return try {
            client.authGet<List<ParentChildReport>>("$BASE_URL/me/children")
        } catch (e: Exception) {
            println("ReportsService ERROR (getChildren): ${e.message}")
            emptyList()
        }
    }

    suspend fun getAttendanceHistory(studentId: Int): List<AttendanceHistoryRecord> {
        return try {
            client.authGet<List<AttendanceHistoryRecord>>("$BASE_URL/me/children/$studentId/attendance")
        } catch (e: Exception) {
            println("ReportsService ERROR (getAttendance): ${e.message}")
            emptyList()
        }
    }
}

@Serializable
data class ParentChildReport(
    val parentesco: String,
    val student: StudentReport
)

@Serializable
data class StudentReport(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val sectionId: Int? = null
)
