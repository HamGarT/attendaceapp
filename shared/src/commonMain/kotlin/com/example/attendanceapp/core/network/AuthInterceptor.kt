package com.example.attendanceapp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createAuthHttpClient(): HttpClient {
    return createPlatformHttpClient().config {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
}

suspend inline fun <reified T> HttpClient.authGet(
    url: String,
    noinline block: (HttpRequestBuilder.() -> Unit)? = null
): T {
    var token = TokenHolder.token
    println("AuthInterceptor: GET $url | token=${token?.take(20)}...")

    var call = get(url) {
        block?.invoke(this)
        token?.let { header("Authorization", "Bearer $it") }
    }

    println("AuthInterceptor: status=${call.status}")

    if (call.status == HttpStatusCode.Unauthorized) {
        println("AuthInterceptor: 401 -> attempting refresh...")
        val refreshed = RefreshTokenService.refreshToken()
        if (refreshed) {
            token = TokenHolder.token
            println("AuthInterceptor: refresh OK, retrying...")
            call = get(url) {
                block?.invoke(this)
                token?.let { header("Authorization", "Bearer $it") }
            }
            println("AuthInterceptor: retry status=${call.status}")
        } else {
            println("AuthInterceptor: refresh FAILED -> clearing session")
            TokenHolder.clear()
            throw Exception("Sesion expirada. Inicia sesion de nuevo.")
        }
    }

    if (call.status != HttpStatusCode.OK) {
        throw Exception("Error del servidor: ${call.status.value}")
    }

    return call.body()
}
