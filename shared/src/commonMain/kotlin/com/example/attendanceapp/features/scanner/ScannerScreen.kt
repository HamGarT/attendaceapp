package com.example.attendanceapp.features.scanner


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.draw.clip
import com.example.attendanceapp.features.scanner.presentation.ScannerViewModel
import kotlinx.coroutines.delay
import org.publicvalue.multiplatform.qrcode.CodeType
import org.publicvalue.multiplatform.qrcode.ScannerWithPermissions


@Composable
fun ScannerScreen(viewModel: ScannerViewModel = ScannerViewModel()) {
    val backgroundColor = Color(0xFFF7F9ED) // Crema
    val darkCardColor = Color(0xFF1E231C)   // Oscuro
    val neonGreen = Color(0xFFB4F836)       // Verde neón

    val uiState by viewModel.uiState.collectAsState()
    var scannedOnce by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = "Scan Class",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            IconButton(onClick = { /* Encender flash */ }) {
                Icon(Icons.Default.FlashlightOn, contentDescription = "Linterna")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Apunta la cámara al código QR\ndel estudiante para registrar su asistencia.",
            textAlign = TextAlign.Center,
            color = Color.DarkGray,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Mostrar estado de carga o mensaje
        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Yellow.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Registrando asistencia...",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }

        if (uiState.successMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Green.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Éxito",
                    tint = Color.Green,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "✓ ${uiState.successMessage}",
                    fontSize = 14.sp,
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )
            }

            LaunchedEffect(uiState.successMessage) {
                delay(3000)
                viewModel.clearMessages()
                scannedOnce = false
            }
        }

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Error",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "✗ ${uiState.errorMessage}",
                    fontSize = 14.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }

            LaunchedEffect(uiState.errorMessage) {
                delay(4000)
                viewModel.clearMessages()
                scannedOnce = false
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(32.dp))
                .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {

            ScannerWithPermissions(
                modifier = Modifier.fillMaxSize(),
                onScanned = { qrResult ->
                    if (!scannedOnce && !uiState.isLoading) {
                        scannedOnce = true
                        try {
                            val studentId = qrResult.toIntOrNull()
                            if (studentId != null) {
                                println("¡Alumno escaneado!: $studentId")
                                viewModel.registerAttendance(studentId)
                            } else {
                                println("Error: El código QR no contiene un ID válido: $qrResult")
                            }
                        } catch (e: Exception) {
                            println("Error al procesar código QR: ${e.message}")
                        }
                    }
                    true
                },
                types = listOf(CodeType.QR),
                cameraPosition = org.publicvalue.multiplatform.qrcode.CameraPosition.BACK,
                enableTorch = false,
                permissionDeniedContent = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "¡Uy! No diste permiso a la cámara.", color = Color.White)
                        Text(text = "Ve a los ajustes para activarla.", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            )

        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { /* Abrir modal para buscar por nombre */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = darkCardColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                // Le damos un padding inferior extra para que no choque con tu barra flotante
                .padding(bottom = 80.dp)
        ) {
            Text(
                text = "Ingresar Asistencia Manualmente",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}