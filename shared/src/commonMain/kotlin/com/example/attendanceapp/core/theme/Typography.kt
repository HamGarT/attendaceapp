package com.example.attendanceapp.core.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


// Importa tu clase Res autogenerada (Asegúrate de que el paquete coincida con el tuyo)
import attendanceapp.shared.generated.resources.Res
import attendanceapp.shared.generated.resources.host_grotesk_regular
import attendanceapp.shared.generated.resources.host_grotesk_medium
import attendanceapp.shared.generated.resources.host_grotesk_semi_bold
import attendanceapp.shared.generated.resources.host_grotesk_bold
import org.jetbrains.compose.resources.Font


// 1. Declaras tu nueva Familia Tipográfica usando los archivos .ttf
@Composable
fun getAppTypography(): Typography {

    // 1. Declaramos la familia adentro de la función
    val HostGrotesk = FontFamily(
        Font(Res.font.host_grotesk_regular, FontWeight.Normal),
        Font(Res.font.host_grotesk_medium, FontWeight.Medium),
        Font(Res.font.host_grotesk_semi_bold, FontWeight.SemiBold),
        Font(Res.font.host_grotesk_bold, FontWeight.Bold)
    )

    // 2. Retornamos la tipografía usando esa fuente
    return Typography(
        bodyLarge = TextStyle(
            fontFamily = HostGrotesk,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        titleLarge = TextStyle(
            fontFamily = HostGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        labelMedium = TextStyle(
            fontFamily = HostGrotesk,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    )
}