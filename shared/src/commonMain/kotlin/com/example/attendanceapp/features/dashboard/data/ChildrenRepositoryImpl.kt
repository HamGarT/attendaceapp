package com.example.attendanceapp.features.dashboard.data

import com.example.attendanceapp.features.dashboard.domain.ChildrenRepository

class ChildrenRepositoryImpl(
    private val childrenService: ChildrenService = ChildrenService()
) : ChildrenRepository {

    override suspend fun getChildren(): Result<List<ParentChild>> {
        return try {
            val children = childrenService.getChildren()
            Result.success(children)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChildAttendance(studentId: Int): AttendanceData? {
        return childrenService.getChildAttendance(studentId)
    }
}
