package com.example.attendanceapp.features.notifications.data

import com.example.attendanceapp.core.BuildConfig
import com.example.attendanceapp.core.network.authGet
import com.example.attendanceapp.core.network.createAuthHttpClient

class NotificationsService {

    companion object {
        private const val BASE_URL = BuildConfig.API_URL
    }

    private val client = createAuthHttpClient()

    suspend fun getNotifications(): List<NotificationResponse> {
        println("NotificationsService: Fetching from $BASE_URL/notifications")
        return try {
            val result = client.authGet<List<NotificationResponse>>("$BASE_URL/notifications")
            println("NotificationsService: Got ${result.size} notifications")
            result
        } catch (e: Exception) {
            println("NotificationsService ERROR: ${e.message}")
            emptyList()
        }
    }
}
