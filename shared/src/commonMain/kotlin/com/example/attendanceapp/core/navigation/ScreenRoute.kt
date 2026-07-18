package com.example.attendanceapp.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class ScreenRoute(val title: String, val icon: ImageVector) {
    Dashboard("Dashboard", Icons.Default.Home),
    Reports("Reportes", Icons.Default.BarChart),
    Scanner("Scanner QR", Icons.Default.QrCodeScanner),
    Notifications("Avisos", Icons.Default.Notifications),
    Profile("Perfil", Icons.Default.Person)
}

enum class UserRole {
    PADRE,
    AUXILIAR
}

// 👉 AGREGAMOS ESTO: Una función que devuelve la lista exacta según el rol
fun getMenuTabsForRole(role: UserRole): List<ScreenRoute> {
    return when (role) {
        UserRole.AUXILIAR -> listOf(
            ScreenRoute.Scanner,
            ScreenRoute.Profile
        )
        UserRole.PADRE -> listOf(
            ScreenRoute.Dashboard,
            ScreenRoute.Reports,
            ScreenRoute.Notifications, // Asumiendo que "Alertas" es Notifications
            ScreenRoute.Profile
        )
    }
}