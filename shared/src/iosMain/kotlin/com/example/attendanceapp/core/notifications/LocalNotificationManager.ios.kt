package com.example.attendanceapp.core.notifications

import platform.Foundation.NSLog
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter

actual class LocalNotificationManager {

    private val center: UNUserNotificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    actual fun createNotificationChannel() {
        center.requestAuthorizationWithOptions(4u) { granted, error ->
            if (granted) {
                NSLog("Notification permission granted")
            } else {
                NSLog("Notification permission denied")
            }
        }
    }

    actual fun showNotification(title: String, message: String, notificationId: Int) {
        val content = UNMutableNotificationContent()
        content.setTitle(title)
        content.setBody(message)

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "notification_$notificationId",
            content = content,
            trigger = null
        )

        center.addNotificationRequest(request) { error ->
            if (error != null) {
                NSLog("Error showing notification: ${error.localizedDescription}")
            }
        }
    }

    actual fun requestPermission() {
        createNotificationChannel()
    }
}

actual fun createLocalNotificationManager(): LocalNotificationManager = LocalNotificationManager()
