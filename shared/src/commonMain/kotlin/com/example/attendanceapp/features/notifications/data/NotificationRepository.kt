package com.example.attendanceapp.features.notifications.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationRepository {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val notificationsService = NotificationsService()

    suspend fun loadFromEndpoint() {
        println("NotificationRepository: Loading from endpoint...")
        val responses = notificationsService.getNotifications()
        val items = responses.map { it.toNotificationItem() }

        val current = _notifications.value.toMutableList()
        for (item in items) {
            if (current.none { it.id == item.id }) {
                current.add(item)
            }
        }
        current.sortByDescending { it.createdAt }
        _notifications.value = current
        println("NotificationRepository: Loaded ${items.size} from endpoint, total=${_notifications.value.size}")
    }

    fun addNotification(item: NotificationItem) {
        val current = _notifications.value.toMutableList()
        if (current.none { it.id == item.id }) {
            current.add(0, item)
            _notifications.value = current
            println("NotificationRepository: Added notification #${item.id}: ${item.title}")
        }
    }

    suspend fun markAsRead(id: Int) {
        val success = notificationsService.markAsRead(id)
        if (success) {
            val current = _notifications.value.toMutableList()
            val index = current.indexOfFirst { it.id == id }
            if (index != -1) {
                // Le ponemos un valor ficticio o la fecha real para que deje de ser "unread"
                val updated = current[index].copy(readAt = "read")
                current[index] = updated
                _notifications.value = current
            }
        }
    }

    suspend fun markAllAsRead() {
        val success = notificationsService.markAllAsRead()
        if (success) {
            val current = _notifications.value.map { it.copy(readAt = "read") }
            _notifications.value = current
        }
    }

    fun getUnreadCount(): Int {
        return _notifications.value.count { it.isUnread }
    }

    fun clear() {
        _notifications.value = emptyList()
    }
}

fun NotificationResponse.toNotificationItem(): NotificationItem {
    return NotificationItem(
        id = id,
        title = title,
        message = message,
        type = type,
        studentId = data.studentId,
        attendanceId = data.attendanceId,
        attendanceTipo = data.tipo,
        createdAt = createdAt,
        readAt = readAt
    )
}
