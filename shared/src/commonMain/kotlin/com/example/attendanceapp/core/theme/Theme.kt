package com.example.attendanceapp.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppLightColorScheme = lightColorScheme(
    primary = Color(0xFFFEB413),          // Verde Neón
    secondary = Color(0xFF795A16),
    onPrimary = Color(0xFF1E231C),        // Verde Oscuro
    background = Color(0xFFF7F9ED),       // Crema
    surface = Color.White,                // Tarjetas blancas
    onSurface = Color(0xFF1E231C)         // Texto general oscuro

)

@Composable
fun AttendanceAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppLightColorScheme,
        typography = getAppTypography(),
        content = content
    )
}