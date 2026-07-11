package com.example.attendanceapp.features.notifications.data

import kotlinx.serialization.Serializable

@Serializable
data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val type: String,
    val studentId: Int? = null,
    val attendanceId: Int? = null,
    val attendanceTipo: String? = null,
    val createdAt: String,
    val readAt: String? = null
) {
    val isUnread: Boolean get() = readAt == null
}
