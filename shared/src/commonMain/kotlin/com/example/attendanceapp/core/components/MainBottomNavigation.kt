package com.example.attendanceapp.core.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.attendanceapp.core.navigation.ScreenRoute

@Composable
fun MainBottomNavigation(
    currentRoute: ScreenRoute,
    tabs: List<ScreenRoute>,
    onNavigate: (ScreenRoute) -> Unit
) {
    val darkBackground = Color(0xFF1E231C)
    val neonGreen = MaterialTheme.colorScheme.primary
    val unselectedIconColor = Color.White.copy(alpha = 0.5f)

    // Surface actúa como nuestra "isla" flotante
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            // Este padding es el secreto para que flote y no toque los bordes
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .height(72.dp),
        shape = RoundedCornerShape(36.dp), // Bordes 100% redondeados (estilo píldora)
        color = darkBackground,
        shadowElevation = 8.dp // Le da una sombra sutil para que resalte sobre el fondo
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { screen ->
                val isSelected = currentRoute == screen

                // Cada botón del menú
                Box(
                    modifier = Modifier
                        .size(52.dp) // Tamaño del círculo
                        .background(
                            color = if (isSelected) neonGreen else Color.Transparent,
                            shape = CircleShape
                        )
                        // Hace que el icono sea clickeable sin el efecto cuadrado feo de ripple
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onNavigate(screen) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        // Si está seleccionado, el icono se vuelve oscuro, si no, blanco transparente
                        tint = if (isSelected) darkBackground else unselectedIconColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}