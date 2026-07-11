package com.example.attendanceapp.features.dashboard.data

class ChildrenRepository(
    private val childrenService: ChildrenService = ChildrenService()
) {
    suspend fun getChildren(): Result<List<ParentChild>> {
        return try {
            val children = childrenService.getChildren()
            Result.success(children)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChildAttendance(studentId: Int): AttendanceData? {
        return childrenService.getChildAttendance(studentId)
    }
}
