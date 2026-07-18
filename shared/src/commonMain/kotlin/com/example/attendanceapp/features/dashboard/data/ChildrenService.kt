package com.example.attendanceapp.features.dashboard.data

import com.example.attendanceapp.core.BuildConfig
import com.example.attendanceapp.core.network.authGet
import com.example.attendanceapp.core.network.createAuthHttpClient

class ChildrenService {

    companion object {
        private const val BASE_URL = BuildConfig.API_URL
    }

    private val client = createAuthHttpClient()

    suspend fun getChildren(): List<ParentChild> {
        return try {
            val result = client.authGet<List<ParentChild>>("$BASE_URL/me/children")
            println("ChildrenService: Got ${result.size} children")
            result
        } catch (e: Exception) {
            println("ChildrenService ERROR: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getChildAttendance(studentId: Int): AttendanceData? {
        return try {
            println("ChildrenService: Fetching attendance for student $studentId")

            // 1. Pedimos la lista usando el nuevo modelo que tiene "id"
            val result = client.authGet<List<AttendanceHistoryResponse>>("$BASE_URL/me/children/$studentId/attendance")

            // 2. Tomamos el primero (el más reciente de hoy) y lo mapeamos a AttendanceData
            result.firstOrNull()?.let { historyRecord ->
                AttendanceData(
                    studentId = historyRecord.studentId,
                    attendanceId = historyRecord.id, // Transformamos "id" en "attendanceId"
                    tipo = historyRecord.tipo
                )
            }
        } catch (e: Exception) {
            println("ChildrenService ERROR (Attendance): ${e.message}")
            null
        }
    }
}
