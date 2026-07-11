package com.example.attendanceapp.core.notifications

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.lang.ref.WeakReference

actual class LocalNotificationManager private constructor(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    actual fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Asistencia",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones de asistencia de tus hijos"
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    actual fun showNotification(title: String, message: String, notificationId: Int) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(context).notify(notificationId, builder.build())
            }
        } else {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        }
    }

    actual fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                val activity = currentActivityRef?.get() ?: return
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "attendance_channel"
        private var instance: LocalNotificationManager? = null
        private var currentActivityRef: WeakReference<Activity>? = null

        fun init(context: Context) {
            if (instance == null) {
                instance = LocalNotificationManager(context.applicationContext)
            }
        }

        fun bindActivity(activity: Activity) {
            currentActivityRef = WeakReference(activity)
        }

        fun unbindActivity(activity: Activity) {
            val current = currentActivityRef?.get()
            if (current === activity) {
                currentActivityRef = null
            }
        }

        fun getInstance(): LocalNotificationManager {
            return instance ?: throw IllegalStateException("LocalNotificationManager not initialized")
        }
    }
}

actual fun createLocalNotificationManager(): LocalNotificationManager = LocalNotificationManager.getInstance()
