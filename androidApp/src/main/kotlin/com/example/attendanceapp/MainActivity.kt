package com.example.attendanceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.attendanceapp.core.network.AppContext
import com.example.attendanceapp.core.notifications.LocalNotificationManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        AppContext.init(applicationContext)
        LocalNotificationManager.init(applicationContext)
        LocalNotificationManager.bindActivity(this)

        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()
        LocalNotificationManager.bindActivity(this)
    }

    override fun onDestroy() {
        LocalNotificationManager.unbindActivity(this)
        super.onDestroy()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
