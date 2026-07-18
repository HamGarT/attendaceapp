package com.example.attendanceapp.features.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendanceapp.features.reports.data.ParentChildReport
import com.example.attendanceapp.features.reports.data.StudentStatistics
import com.example.attendanceapp.features.reports.presentation.ReportsViewModel

@Composable
fun ReportsScreen(viewModel: ReportsViewModel) {
    val backgroundColor = Color(0xFFF7F9ED)

    val children by viewModel.children.collectAsState()
    val selectedIndex by viewModel.selectedChildIndex.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val monthlyData by viewModel.monthlyData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(top = 24.dp, bottom = 100.dp)
    ) {
        TopBarSection()

        Spacer(modifier = Modifier.height(24.dp))

        HeaderSection()

        Spacer(modifier = Modifier.height(24.dp))

        ChildSelectorTabs(
            children = children,
            selectedIndex = selectedIndex,
            onChildSelected = { viewModel.selectChild(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            YearlyTrendCard(monthlyData = monthlyData)

            Spacer(modifier = Modifier.height(16.dp))

            StatsCard(statistics = statistics)

            Spacer(modifier = Modifier.height(32.dp))

            MonthlyReportsSection()
        }
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
        )
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
            text = "Reporte de asistencia",
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
private fun ChildSelectorTabs(
    children: List<ParentChildReport>,
    selectedIndex: Int,
    onChildSelected: (Int) -> Unit
) {
    val darkText = Color(0xFF1E231C)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        children.forEachIndexed { index, parentChild ->
            val isSelected = index == selectedIndex
            val name = "${parentChild.student.nombres} ${parentChild.student.apellidos}"
            val shortName = name.split(" ").take(2).joinToString(" ")

            Surface(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFEAECE2),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clickable { onChildSelected(index) }
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                if (isSelected) Color.Gray else Color.LightGray,
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = shortName,
                        fontWeight = FontWeight.Bold,
                        color = darkText,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (children.isEmpty()) {
            Surface(
                color = Color(0xFFEAECE2),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sin hijos",
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun YearlyTrendCard(monthlyData: List<Pair<String, Int>>) {
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

                Surface(
                    color = Color(0xFFF7F9ED),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "2024 - 2025", fontSize = 12.sp, color = Color.DarkGray)
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (monthlyData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay datos disponibles",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                val maxValue = monthlyData.maxOfOrNull { it.second } ?: 1
                val barMaxHeight = 120.dp

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(barMaxHeight + 30.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    monthlyData.forEach { (month, count) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = count.toString(),
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(barMaxHeight * (count.toFloat() / maxValue))
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = month,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsCard(statistics: StudentStatistics?) {
    val attendanceRate = statistics?.let {
        "${it.attendanceRate}%"
    } ?: "--"

    val daysPresent = statistics?.daysPresent?.toString() ?: "--"
    val daysAbsent = statistics?.daysAbsent?.toString() ?: "--"
    val tardies = statistics?.tardies?.toString() ?: "--"

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "Current Attendance Rate", color = Color.White, fontSize = 14.sp)
            Text(text = attendanceRate, color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)

            Spacer(modifier = Modifier.height(16.dp))

            StatRow(label = "Days Present", value = daysPresent)
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
            StatRow(label = "Days Absent", value = daysAbsent)
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
            StatRow(label = "Tardies", value = tardies)
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
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
            Text(
                text = "Coming soon",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Download reports coming soon",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}
