package com.example.attendanceapp.features.dashboard.presentation
import com.example.attendanceapp.core.notifications.PushNotificationService

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.features.dashboard.data.AttendanceData
import com.example.attendanceapp.features.dashboard.data.AttendanceWebSocket
import com.example.attendanceapp.features.dashboard.data.ChildrenRepositoryImpl
import com.example.attendanceapp.features.dashboard.data.ParentChild
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val childrenRepositoryImpl: ChildrenRepositoryImpl = ChildrenRepositoryImpl(),
    private val attendanceWebSocket: AttendanceWebSocket,
    private val pushNotificationService: PushNotificationService
) : ViewModel() {

    private val _children = MutableStateFlow<List<ParentChild>>(emptyList())
    val children: StateFlow<List<ParentChild>> = _children.asStateFlow()

    private val _attendanceStatus = MutableStateFlow<Map<Int, AttendanceData>>(emptyMap())
    val attendanceStatus: StateFlow<Map<Int, AttendanceData>> = _attendanceStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchChildren()
        collectAttendanceUpdates()
    }

    private fun fetchChildren() {
        viewModelScope.launch {
            _isLoading.value = true
            println("DashboardViewModel: fetchChildren called")

            val result = childrenRepositoryImpl.getChildren()
            result.fold(
                onSuccess = { children ->

                    _children.value = children

                    // NUEVO: Buscar la asistencia inicial de cada hijo
                    fetchInitialAttendance(children)

                    _error.value = null
                },
                onFailure = { e ->
                    println("DashboardViewModel ERROR: ${e.message}")
                    _error.value = e.message ?: "Error al cargar hijos"
                }
            )
            _isLoading.value = false
        }
    }

    private suspend fun fetchInitialAttendance(children: List<ParentChild>) {
        val initialMap = mutableMapOf<Int, AttendanceData>()

        children.forEach { parentChild ->
            val studentId = parentChild.student.id
            val attendance = childrenRepositoryImpl.getChildAttendance(studentId)

            if (attendance != null) {
                initialMap[studentId] = attendance
            }
        }

        // Actualizamos la UI de golpe con los datos iniciales reales
        _attendanceStatus.value = initialMap
    }

    private fun collectAttendanceUpdates() {
        viewModelScope.launch {
            attendanceWebSocket.attendanceUpdates.collect { attendanceData ->
                val currentMap = _attendanceStatus.value.toMutableMap()
                currentMap[attendanceData.studentId] = attendanceData
                _attendanceStatus.value = currentMap
            }
        }
    }

    fun registerDeviceForNotifications() {
        viewModelScope.launch {
            val token = pushNotificationService.getDeviceToken()
            if (token == null) {
                // log or update UI state: couldn't get token, maybe retry
                return@launch
            }
            sendTokenToBackend(token)
        }
    }

    /** Call once (e.g. in init {}) to keep the backend in sync when the token rotates. */
    fun observeTokenRefresh() {
        viewModelScope.launch {
            MyFirebaseMessagingService.tokenFlow.collect { newToken ->
                sendTokenToBackend(newToken)
            }
        }
    }

    private suspend fun sendTokenToBackend(token: String) {
        try {
            profileRepository.updateFcmToken(token)
        } catch (e: Exception) {
            // log: failed to sync FCM token, consider retry/queue
        }
    }
}
