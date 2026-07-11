package com.example.attendanceapp.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen() {
    // Fondo claro crema como en tu diseño
    val backgroundColor = Color(0xFFF7F9ED)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
    ) {
        // 1. Header (Welcome Back)
        HeaderSection()

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Tarjeta principal oscura (Today's Attendance)
        TodayAttendanceCard()

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Texto de Actividad Reciente
        Text(
            text = "Recent Activity",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Aquí iría tu lista de alumnos recientes...
    }
}

@Composable
fun HeaderSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Placeholder circular para la foto de perfil
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.Gray, shape = RoundedCornerShape(25.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "Welcome Back",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Principal Current",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun TodayAttendanceCard() {

    Card(
        colors = CardDefaults.cardColors(),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Today's Attendance",
                    color = Color.White,
                    fontSize = 16.sp
                )
                // Botón o etiqueta verde de +1.2%
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "↗ +1.2%",
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "92.4%",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold
            )

            // Aquí iría el gráfico de barras (necesita una librería o dibujarse en Canvas)
        }
    }
}