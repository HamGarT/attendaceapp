package com.example.attendanceapp.features.reports



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReportsScreen() {
    val backgroundColor = Color(0xFFF7F9ED) // Fondo crema

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(top = 24.dp, bottom = 100.dp) // Padding inferior para la barra flotante
    ) {
        // 1. Top Bar (Reutilizada de la otra vista)
        TopBarSection()

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Título de la página
        HeaderSection()

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Pestañas selectoras de los hijos (Leo vs Mia)
        ChildSelectorTabs()

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Tarjeta del Gráfico Anual
        YearlyTrendCard()

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Tarjeta Oscura de Estadísticas
        StatsCard()

        Spacer(modifier = Modifier.height(32.dp))

        // 6. Lista de Reportes Mensuales
        MonthlyReportsSection()
    }
}

@Composable
private fun TopBarSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(Color.LightGray, CircleShape)
        ) // Foto de perfil
        Text(
            text = "Lumina",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4A6B22)
        )
        IconButton(onClick = { }) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones", tint = Color(0xFF4A6B22))
        }
    }
}

@Composable
private fun HeaderSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Attendance Reports",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E231C)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "View and download historical attendance records.",
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
private fun ChildSelectorTabs() {
    val neonGreen = Color(0xFFB4F836)
    val unselectedColor = Color(0xFFEAECE2) // Un crema más oscurito
    val darkText = Color(0xFF1E231C)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tab Seleccionado (Leo)
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.weight(1f).height(48.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(24.dp).background(Color.Gray, CircleShape)) // Foto miniatura
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Leo Connor", fontWeight = FontWeight.Bold, color = darkText)
            }
        }

        // Tab No Seleccionado (Mia)
        Surface(
            color = unselectedColor,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.weight(1f).height(48.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(24.dp).background(Color.LightGray, CircleShape)) // Foto miniatura
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Mia Connor", fontWeight = FontWeight.Bold, color = darkText)
            }
        }
    }
}

@Composable
private fun YearlyTrendCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Yearly Trend", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                // Dropdown falso
                Surface(
                    color = Color(0xFFF7F9ED),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "2023 - 2024", fontSize = 12.sp, color = Color.DarkGray)
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(150.dp)) // Espacio donde iría el gráfico real

            // Etiquetas del eje X
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Sep", "Oct", "Nov", "Dec", "Jan", "Feb").forEach { month ->
                    Text(text = month, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun StatsCard() {

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "Current Attendance Rate", color = Color.White, fontSize = 14.sp)
            Text(text = "94.3%", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)

            Spacer(modifier = Modifier.height(16.dp))

            StatRow(label = "Days Present", value = "112", isHighlighted = false)
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
            StatRow(label = "Days Absent", value = "6", isHighlighted = false)
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
            StatRow(label = "Tardies", value = "2", isHighlighted = false)
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, isHighlighted: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White, fontSize = 16.sp)
        Text(text = value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MonthlyReportsSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Monthly Reports", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "Download All (ZIP)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A6B22))
        }

        Spacer(modifier = Modifier.height(16.dp))

        ReportItemCard(month = "February", year = "2024", date = "Mar 1, 2024", size = "1.2 MB")
        Spacer(modifier = Modifier.height(12.dp))
        ReportItemCard(month = "January", year = "2024", date = "Feb 1, 2024", size = "1.1 MB")
        Spacer(modifier = Modifier.height(12.dp))
        ReportItemCard(month = "December", year = "2023", date = "Jan 1, 2024", size = "1.5 MB")
    }
}

@Composable
private fun ReportItemCard(month: String, year: String, date: String, size: String) {
    val cardBg = Color(0xFFF3F5EB) // Verde agrisado muy claro
    val darkText = Color(0xFF1E231C)

    Surface(
        color = cardBg,
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono PDF
            Surface(
                color = Color(0xFFE5E8D8),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PictureAsPdf,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = darkText
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Textos
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "$month\n$year Report", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkText, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Generated $date • $size", fontSize = 12.sp, color = Color.DarkGray)
            }

            // Ojo de previsualización
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Visibility, contentDescription = "Preview", tint = darkText)
            }

            // Botón de descarga
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = darkText),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Download", color = Color.White)
            }
        }
    }
}