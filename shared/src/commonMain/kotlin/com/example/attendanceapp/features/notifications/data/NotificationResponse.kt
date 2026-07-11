package com.example.attendanceapp.features.notifications.data

import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    val id: Int,
    val userId: Int,
    val type: String,
    val title: String,
    val message: String,
    val data: NotificationDataPayload,
    val createdAt: String,
    val readAt: String? = null
)

@Serializable
data class NotificationDataPayload(
    val tipo: String? = null,
    val studentId: Int? = null,
    val attendanceId: Int? = null
)
