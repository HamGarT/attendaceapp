package com.example.attendanceapp.features.scanner.data

import com.example.attendanceapp.core.BuildConfig
import com.example.attendanceapp.core.network.TokenHolder
import com.example.attendanceapp.core.network.createPlatformHttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AttendanceService {

    companion object {
        private const val BASE_URL = BuildConfig.API_URL
    }

    private val client = createPlatformHttpClient().config {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun registerAttendance(studentId: Int): AttendanceResponse {
        return try {
            val token = TokenHolder.getBearerToken()
            val response = client.post("$BASE_URL/attendance") {
                contentType(ContentType.Application.Json)
                if (token != null) {
                    header("Authorization", token)
                }
                setBody(AttendanceRequest(
                    studentId = studentId,
                    tipo = "INGRESO",
                    observacion = "Llegó a tiempo"
                ))
            }
            response.body<AttendanceResponse>()
        } catch (e: Exception) {
            AttendanceResponse(
                success = false,
                error = e.message ?: "Error al registrar asistencia"
            )
        }
    }
}

