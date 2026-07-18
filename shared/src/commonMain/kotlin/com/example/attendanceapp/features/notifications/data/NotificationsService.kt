package com.example.attendanceapp.features.notifications.data

import com.example.attendanceapp.core.BuildConfig
import com.example.attendanceapp.core.network.authGet
import com.example.attendanceapp.core.network.authPatch
import com.example.attendanceapp.core.network.authPost
import com.example.attendanceapp.core.network.createAuthHttpClient
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

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

    suspend fun markAsRead(notificationId: Int): Boolean {
        return try {
            val response = client.authPatch<HttpResponse>("$BASE_URL/notifications/$notificationId/read")
            response.status.value in 200..299
        } catch (e: Exception) {
            println("NotificationsService ERROR markAsRead: ${e.message}")
            false
        }
    }

    suspend fun markAllAsRead(): Boolean {
        return try {
            val response = client.authPatch<HttpResponse>("$BASE_URL/notifications/read-all")
            response.status.value in 200..299
        } catch (e: Exception) {
            println("NotificationsService ERROR markAllAsRead: ${e.message}")
            false
        }
    }
}
