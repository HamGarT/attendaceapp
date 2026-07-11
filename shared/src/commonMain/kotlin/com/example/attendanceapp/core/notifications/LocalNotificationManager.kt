package com.example.attendanceapp.core.notifications

expect class LocalNotificationManager {
    fun createNotificationChannel()
    fun showNotification(title: String, message: String, notificationId: Int)
    fun requestPermission()
}

expect fun createLocalNotificationManager(): LocalNotificationManager
