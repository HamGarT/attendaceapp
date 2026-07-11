package com.example.attendanceapp.features.auth.data

import com.example.attendanceapp.core.BuildConfig
import com.example.attendanceapp.core.network.createPlatformHttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AuthService {

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

    suspend fun login(email: String, password: String, rememberMe: Boolean = true): LoginResponse {
        return try {
            val response = client.post("$BASE_URL/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(
                    email = email,
                    password = password,
                    rememberMe = rememberMe
                ))
            }
            response.body<LoginResponse>()
        } catch (e: Exception) {
            throw Exception(e.message ?: "Error de conexion")
        }
    }
}
