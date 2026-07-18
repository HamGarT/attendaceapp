package com.example.attendanceapp.features.dashboard


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendanceapp.features.dashboard.data.AttendanceData
import com.example.attendanceapp.features.dashboard.data.ParentChild
import com.example.attendanceapp.features.dashboard.data.Section
import com.example.attendanceapp.features.dashboard.presentation.DashboardViewModel

@Composable
fun ParentDashboardScreen(
    userName: String = "Usuario",
    viewModel: DashboardViewModel
) {
    val children by viewModel.children.collectAsState()
    val attendanceStatus by viewModel.attendanceStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val backgroundColor = Color(0xFFF7F9ED)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(top = 24.dp, bottom = 100.dp)
    ) {
        TopBarSection()

        Spacer(modifier = Modifier.height(24.dp))

        GreetingSection(userName = userName)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Mis hijos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E231C),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            ChildrenCardsRow(children = children, attendanceStatus = attendanceStatus)
        }

        Spacer(modifier = Modifier.height(32.dp))

        HouseholdAttendanceCard()

        Spacer(modifier = Modifier.height(24.dp))

        UpcomingEventCard()
    }
}

@Composable
fun TopBarSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.LightGray, CircleShape)
        )

        Text(
            text = "Lumina",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4A6B22)
        )

        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificaciones",
                tint = Color(0xFF4A6B22)
            )
        }
    }
}

@Composable
fun GreetingSection(userName: String) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Hola, $userName",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E231C)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Aqui tienes el resumen de asistencia de tus hijos.",
            fontSize = 16.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun ChildrenCardsRow(
    children: List<ParentChild>,
    attendanceStatus: Map<Int, AttendanceData>
) {
    if (children.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No se encontraron hijos registrados.",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            items(children) { parentChild ->
                val attendance = attendanceStatus[parentChild.student.id]
                ChildCard(
                    name = "${parentChild.student.nombres} ${parentChild.student.apellidos}",
                    gradeAndSection =  parentChild.student.section?.grade +" Grado"+" ● "+ parentChild.student.section?.name,
                    attendance = attendance
                )
            }
        }
    }
}

@Composable
fun ChildCard(
    name: String,
    gradeAndSection:  String,
    attendance: AttendanceData?
) {
    val hasAttended = attendance?.tipo == "INGRESO"
    val statusColor = if (hasAttended) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
    val statusIcon = if (hasAttended) Icons.Default.CheckCircle else Icons.Default.CheckCircle
    val statusIconTint = if (hasAttended) MaterialTheme.colorScheme.primary else Color(0xFFEF4444)
    val statusText = if (hasAttended) "Presente" else if (attendance != null) "Ausente" else "Sin registro"
    val statusTextColor = if (hasAttended) Color(0xFF166534) else if (attendance != null) Color(0xFF991B1B) else Color(0xFF6B7280)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.width(280.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.LightGray, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = gradeAndSection, fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(statusColor, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = statusText,
                        tint = statusIconTint,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = statusText, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = statusTextColor)
                }
                if (attendance != null) {
                    Text(text = attendance.tipo, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun HouseholdAttendanceCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Resumen de Asistencia",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Mes Actual",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "--",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E231C)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2C3225))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun UpcomingEventCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = "Evento",
                    tint = Color(0xFF1E231C)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Proximo",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E231C)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Reunion de Apoderados",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E231C)
            )
            Text(
                text = "Por confirmar",
                fontSize = 16.sp,
                color = Color(0xFF4A6B22)
            )
        }
    }
}
