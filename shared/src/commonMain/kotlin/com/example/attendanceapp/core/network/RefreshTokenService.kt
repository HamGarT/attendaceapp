package com.example.attendanceapp.core.network

import com.example.attendanceapp.core.BuildConfig
import com.example.attendanceapp.features.auth.data.LoginResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class RefreshRequest(
    val refreshToken: String
)

object RefreshTokenService {

    private const val BASE_URL = BuildConfig.API_URL

    private val client = createPlatformHttpClient().config {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun refreshToken(): Boolean {
        val currentRefreshToken = TokenHolder.refreshToken
        if (currentRefreshToken.isNullOrBlank()) {
            println("RefreshTokenService: No refresh token available")
            return false
        }

        return try {
            println("RefreshTokenService: Calling /api/auth/refresh")
            val response = client.post("$BASE_URL/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshRequest(refreshToken = currentRefreshToken))
            }

            println("RefreshTokenService: status=${response.status}")

            if (response.status.value == 200) {
                val body = response.body<LoginResponse>()
                if (body.token != null) {
                    TokenHolder.saveTokens(body.token, body.refreshToken ?: currentRefreshToken)
                    println("RefreshTokenService: Token refreshed successfully")
                    true
                } else {
                    println("RefreshTokenService: No token in response")
                    false
                }
            } else if (response.status.value == 401) {
                println("RefreshTokenService: Refresh token invalid (401), clearing session")
                TokenHolder.clear()
                false
            } else {
                println("RefreshTokenService: Server error ${response.status}, keeping session")
                false
            }
        } catch (e: Exception) {
            println("RefreshTokenService ERROR: ${e.message}")
            false
        }
    }
}
