package com.example.attendanceapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.attendanceapp.core.components.MainBottomNavigation
import com.example.attendanceapp.core.navigation.ScreenRoute
import com.example.attendanceapp.core.navigation.UserRole
import com.example.attendanceapp.core.navigation.getMenuTabsForRole
import com.example.attendanceapp.core.network.AppContext
import com.example.attendanceapp.core.network.TokenHolder
import com.example.attendanceapp.core.notifications.createLocalNotificationManager
import com.example.attendanceapp.core.theme.AttendanceAppTheme
import com.example.attendanceapp.features.auth.data.AuthRepositoryImpl
import com.example.attendanceapp.features.auth.domain.User
import com.example.attendanceapp.features.auth.presentation.LoginScreen
import com.example.attendanceapp.features.auth.presentation.LoginViewModel
import com.example.attendanceapp.features.dashboard.ParentDashboardScreen
import com.example.attendanceapp.features.dashboard.data.AttendanceWebSocket
import com.example.attendanceapp.features.dashboard.presentation.DashboardViewModel
import com.example.attendanceapp.features.notifications.NotificationsScreen
import com.example.attendanceapp.features.notifications.presentation.NotificationsViewModel
import com.example.attendanceapp.features.profile.ProfileScreen
import com.example.attendanceapp.features.profile.presentation.ProfileViewModel
import com.example.attendanceapp.features.reports.ReportsScreen
import com.example.attendanceapp.features.reports.presentation.ReportsViewModel
import com.example.attendanceapp.features.scanner.ScannerScreen
import com.example.attendanceapp.features.scanner.presentation.ScannerViewModel


@Composable
@Preview
fun App() {
    AttendanceAppTheme {
        var isLoggedIn by remember { mutableStateOf(false) }
        var currentUser by remember { mutableStateOf<User?>(null) }
        var isCheckingSession by remember { mutableStateOf(true) }

        val loginViewModel = remember {
            LoginViewModel(AuthRepositoryImpl())
        }

        val notificationManager = remember {
            createLocalNotificationManager()
        }

        val isSessionActive by TokenHolder.sessionActive.collectAsState()

        val attendanceWebSocket = remember {
            AttendanceWebSocket(notificationManager)
        }

        LaunchedEffect(Unit) {
            notificationManager.createNotificationChannel()
            notificationManager.requestPermission()

            if (AppContext.isInitialized()) {
                TokenHolder.loadFromStorage()
                if (TokenHolder.hasValidSession()) {
                    currentUser = User(
                        id = TokenHolder.userId,
                        name = TokenHolder.userName,
                        lastname = TokenHolder.userLastname,
                        email = TokenHolder.userEmail,
                        role = TokenHolder.userRole
                    )
                    isLoggedIn = true
                    println("App: Session restored for ${TokenHolder.userName}")
                } else {
                    println("App: No saved session found")
                }
            }
            isCheckingSession = false
        }

        LaunchedEffect(isSessionActive) {
            if (isCheckingSession) return@LaunchedEffect
            if (!isSessionActive) {
                attendanceWebSocket.disconnect()
                currentUser = null
                isLoggedIn = false
            }
        }

        if (isCheckingSession) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F9ED)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else if (!isLoggedIn) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { user ->
                    currentUser = user
                    isLoggedIn = true
                }
            )
        } else {
            val dashboardViewModel = remember {
                DashboardViewModel(attendanceWebSocket = attendanceWebSocket)
            }

            val notificationsViewModel = remember {
                NotificationsViewModel(attendanceWebSocket)
            }

            val scannerViewModel = remember {
                ScannerViewModel()
            }

            val reportsViewModel = remember {
                ReportsViewModel()
            }

            val profileViewModel = remember {
                ProfileViewModel()
            }

            LaunchedEffect(Unit) {
                attendanceWebSocket.connect()
            }

            val userRole = when (currentUser?.role?.lowercase()) {
                "auxiliar" -> UserRole.AUXILIAR
                else -> UserRole.PADRE
            }
            val allowedTabs = remember(userRole) { getMenuTabsForRole(userRole) }
            var currentScreen by remember { mutableStateOf(allowedTabs.first()) }

            val backgroundColor = Color(0xFFF7F9ED)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        ScreenRoute.Dashboard -> ParentDashboardScreen(
                            userName = currentUser?.name ?: "Usuario",
                            viewModel = dashboardViewModel
                        )
                        ScreenRoute.Reports -> ReportsScreen(viewModel = reportsViewModel)
                        ScreenRoute.Scanner -> ScannerScreen(
                            viewModel = scannerViewModel
                        )
                        ScreenRoute.Notifications -> NotificationsScreen(
                            viewModel = notificationsViewModel
                        )
                        ScreenRoute.Profile -> ProfileScreen(
                            onSignOut = {
                                attendanceWebSocket.disconnect()
                                TokenHolder.clear()
                            },
                            viewModel = profileViewModel
                        )
                    }
                }
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    MainBottomNavigation(
                        currentRoute = currentScreen,
                        tabs = allowedTabs,
                        onNavigate = { newRoute -> currentScreen = newRoute }
                    )
                }
            }
        }
    }
}
