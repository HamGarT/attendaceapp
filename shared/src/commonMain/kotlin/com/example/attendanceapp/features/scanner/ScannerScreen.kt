package com.example.attendanceapp.features.scanner

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val infiniteTransition = rememberInfiniteTransition()
    val linePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse // Va de arriba a abajo y viceversa
        ), label = "scannerLine"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(top = 24.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. HEADER ---
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

        Spacer(modifier = Modifier.height(24.dp))

        // --- 2. CÁMARA CON EFECTO LÁSER (AHORA ARRIBA) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Esto hace que la cámara tome todo el espacio disponible
                .clip(RoundedCornerShape(24.dp))
                .border(4.dp, Color(0xFF2C5E7A), RoundedCornerShape(24.dp)), // Borde similar a tu imagen
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
                                viewModel.registerAttendance(studentId)
                            } else {
                                viewModel.showError("QR Inválido.")
                            }
                        } catch (e: Exception) {
                            viewModel.showError("Error de lectura.")
                        }
                    }
                    false
                },
                types = listOf(CodeType.QR),
                cameraPosition = org.publicvalue.multiplatform.qrcode.CameraPosition.BACK,
                enableTorch = false,
                permissionDeniedContent = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                        Text("¡Uy! No diste permiso a la cámara.", color = Color.White)
                    }
                }
            )

            // Canvas superpuesto para dibujar el láser
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasHeight = size.height
                val canvasWidth = size.width

                // Calculamos la posición de la línea basándonos en la animación (dejando un margen de 10%)
                val margin = canvasHeight * 0.1f
                val availableHeight = canvasHeight - (margin * 2)
                val currentY = margin + (availableHeight * linePosition)

                // Efecto de resplandor (Halo)
                val gradientBrush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, neonGreen.copy(alpha = 0.5f), Color.Transparent),
                    startY = currentY - 30f,
                    endY = currentY + 30f
                )

                drawRect(
                    brush = gradientBrush,
                    topLeft = Offset(0f, currentY - 30f),
                    size = Size(canvasWidth, 60f)
                )

                // Línea central brillante
                drawLine(
                    color = neonGreen,
                    start = Offset(0f, currentY),
                    end = Offset(canvasWidth, currentY),
                    strokeWidth = 4.dp.toPx()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 3. ESTADOS (CARGA / ERROR) ---
        if (uiState.isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFD54F).copy(alpha = 0.8f), RoundedCornerShape(8.dp))
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
                Text(text = "Registrando asistencia...", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.errorMessage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, // Considera cambiar por un ícono de Warning
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
            Spacer(modifier = Modifier.height(16.dp))
        }


        uiState.scannedData?.let { response ->
            val student = response.student
            Card(
                colors = CardDefaults.cardColors(containerColor = darkCardColor),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Éxito",
                            tint = neonGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¡Registro Exitoso!",
                            color = neonGreen,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (student != null) {
                        Text(
                            text = "${student.nombres} ${student.apellidos}",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "DNI: ${student.dni}", color = Color.Gray, fontSize = 16.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .background(neonGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .border(1.dp, neonGreen, RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = response.tipo ?: "INGRESO",
                                color = neonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.clearMessages()
                            scannedOnce = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = neonGreen,
                            contentColor = darkCardColor
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Escanear siguiente alumno", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(96.dp))
    }
}