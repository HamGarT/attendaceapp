package com.example.attendanceapp.core.notifications

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.messaging
import kotlinx.coroutines.flow.Flow

class PushNotificationService {

    suspend fun initialize(): Boolean {
        return try {
            Firebase.messaging.subscribeToTopic("all_users")
            true
        } catch (e: Exception) {
            logError("Error initializing FCM", e)
            false
        }
    }

    /** Unsubscribes from the topic, e.g. on logout. */
    suspend fun shutdown(): Boolean {
        return try {
            Firebase.messaging.unsubscribeFromTopic("all_users")
            true
        } catch (e: Exception) {
            logError("Error unsubscribing from FCM topic", e)
            false
        }
    }

    /** Returns the current FCM token to send to your Express backend, or null on failure. */
    suspend fun getDeviceToken(): String? {
        return try {
            Firebase.messaging.getToken()
        } catch (e: Exception) {
            logError("Error getting FCM token", e)
            null
        }
    }

    /** Deletes the current token, e.g. on logout, so old messages stop arriving. */
    suspend fun deleteDeviceToken(): Boolean {
        return try {
            Firebase.messaging.deleteToken()
            true
        } catch (e: Exception) {
            logError("Error deleting FCM token", e)
            false
        }
    }

    private fun logError(message: String, e: Exception) {
        println("$message: ${e.message}") // swap for Napier or your logger
    }
}