package com.example.attendanceapp.features.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.features.dashboard.data.AttendanceWebSocket
import com.example.attendanceapp.features.notifications.data.NotificationItem
import com.example.attendanceapp.features.notifications.data.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val attendanceWebSocket: AttendanceWebSocket
) : ViewModel() {

    val notifications: StateFlow<List<NotificationItem>> = NotificationRepository.notifications

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    init {
        loadNotifications()
        collectWebSocketNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            NotificationRepository.loadFromEndpoint()
            _unreadCount.value = NotificationRepository.getUnreadCount()
        }
    }

    private fun collectWebSocketNotifications() {
        viewModelScope.launch {
            attendanceWebSocket.notifications.collect {
                _unreadCount.value = NotificationRepository.getUnreadCount()
            }
        }
    }

    fun markAsRead(id: Int) {
        viewModelScope.launch {
            NotificationRepository.markAsRead(id)
            _unreadCount.value = NotificationRepository.getUnreadCount()
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            NotificationRepository.markAllAsRead()
            _unreadCount.value = 0
        }
    }
}
