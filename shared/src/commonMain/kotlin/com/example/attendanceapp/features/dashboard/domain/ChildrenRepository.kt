package com.example.attendanceapp.features.dashboard.domain

import com.example.attendanceapp.features.dashboard.data.AttendanceData
import com.example.attendanceapp.features.dashboard.data.ParentChild

interface ChildrenRepository {
    suspend fun getChildren(): Result<List<ParentChild>>
    suspend fun getChildAttendance(studentId: Int): AttendanceData?
}