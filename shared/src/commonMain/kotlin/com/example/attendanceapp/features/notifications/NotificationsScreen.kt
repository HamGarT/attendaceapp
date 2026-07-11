package com.example.attendanceapp.features.notifications


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendanceapp.features.notifications.data.NotificationItem
import com.example.attendanceapp.features.notifications.presentation.NotificationsViewModel

@Composable
fun NotificationsScreen(viewModel: NotificationsViewModel) {
    val backgroundColor = Color(0xFFF7F9ED)
    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(top = 24.dp)
    ) {
        TopBarSection(
            unreadCount = notifications.count { it.isUnread },
            onMarkAllRead = { viewModel.markAllAsRead() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        FilterChipsSection()

        Spacer(modifier = Modifier.height(16.dp))

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay notificaciones",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notifications) { notification ->
                    NotificationItemCard(
                        notification = notification,
                        onClick = { viewModel.markAsRead(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBarSection(unreadCount: Int, onMarkAllRead: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Notificaciones",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4A6B22)
                )
                if (unreadCount > 0) {
                    Text(
                        text = "$unreadCount sin leer",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        if (unreadCount > 0) {
            TextButton(onClick = onMarkAllRead) {
                Text(
                    text = "Marcar todo leido",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun FilterChipsSection() {
    val filters = listOf("Todas", "Sin leer", "Asistencia", "Sistema")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        items(filters) { filter ->
            val isSelected = filter == "Todas"
            val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFEAECE2)
            val textColor = if (isSelected) Color(0xFF1E231C) else Color.DarkGray

            Surface(
                color = bgColor,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = filter,
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItemCard(notification: NotificationItem, onClick: () -> Unit) {
    val darkText = Color(0xFF1E231C)
    val isAttendance = notification.type == "attendance"
    val iconBgColor = if (notification.isUnread) MaterialTheme.colorScheme.primary else Color(0xFFEAECE2)
    val icon = when {
        notification.attendanceTipo == "INGRESO" -> Icons.Default.CheckCircle
        notification.attendanceTipo == "SALIDA" -> Icons.Default.ExitToApp
        isAttendance -> Icons.Default.Warning
        else -> Icons.Default.Info
    }

    Surface(
        color = if (notification.isUnread) Color(0xFFF0FDF4) else Color.White,
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = iconBgColor,
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = darkText
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatTimeAgo(notification.createdAt),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        if (notification.isUnread) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun formatTimeAgo(isoDate: String): String {
    return try {
        val date = isoDate.substringBefore("T")
        val time = isoDate.substringAfter("T").substringBefore(".")
        "$date $time"
    } catch (e: Exception) {
        isoDate
    }
}
