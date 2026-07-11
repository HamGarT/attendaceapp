package com.example.attendanceapp.features.dashboard.data

import kotlinx.serialization.Serializable

@Serializable
data class WebSocketMessage(
    val type: String,
    val payload: NotificationPayload
)

@Serializable
data class NotificationPayload(
    val id: Int,
    val userId: Int,
    val type: String,
    val title: String,
    val message: String,
    val data: AttendanceData,
    val createdAt: String,
    val readAt: String? = null
)

@Serializable
data class AttendanceData(
    val studentId: Int,
    val attendanceId: Int,
    val tipo: String
)
