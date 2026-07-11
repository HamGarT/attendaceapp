package com.example.attendanceapp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.patch
import io.ktor.client.request.post
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
            println("AuthInterceptor: refresh FAILED -> keeping persisted session and propagating 401")
            throw Exception("Sesion expirada o no renovable. Reintenta o inicia sesion de nuevo.")
        }
    }

    if (call.status != HttpStatusCode.OK) {
        throw Exception("Error del servidor: ${call.status.value}")
    }

    return call.body()
}

suspend inline fun <reified T> HttpClient.authPost(
    url: String,
    noinline block: (HttpRequestBuilder.() -> Unit)? = null
): T {
    var token = TokenHolder.token
    println("AuthInterceptor: POST $url | token=${token?.take(20)}...")

    // 1. Cambiamos get() por post()
    var call = post(url) {
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
            // 2. Cambiamos también el get() por post() en el reintento
            call = post(url) {
                block?.invoke(this)
                token?.let { header("Authorization", "Bearer $it") }
            }
            println("AuthInterceptor: retry status=${call.status}")
        } else {
            println("AuthInterceptor: refresh FAILED -> keeping persisted session and propagating 401")
            throw Exception("Sesion expirada o no renovable. Reintenta o inicia sesion de nuevo.")
        }
    }

    // 3. LA CORRECCIÓN CLAVE: Aceptar cualquier código de éxito (200 al 299)
    // Así ya no va a fallar cuando Express te responda con un 204 No Content
    if (call.status.value !in 200..299) {
        throw Exception("Error del servidor: ${call.status.value}")
    }

    return call.body()
}

suspend inline fun <reified T> HttpClient.authPatch(
    url: String,
    noinline block: (HttpRequestBuilder.() -> Unit)? = null
): T {
    var token = TokenHolder.token
    println("AuthInterceptor: PATCH $url | token=${token?.take(20)}...")

    // Usamos patch() en lugar de post()
    var call = patch(url) {
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
            // Usamos patch() en el reintento también
            call = patch(url) {
                block?.invoke(this)
                token?.let { header("Authorization", "Bearer $it") }
            }
            println("AuthInterceptor: retry status=${call.status}")
        } else {
            println("AuthInterceptor: refresh FAILED -> keeping persisted session and propagating 401")
            throw Exception("Sesion expirada o no renovable. Reintenta o inicia sesion de nuevo.")
        }
    }

    // Validación a prueba de balas para tu 204 No Content
    if (call.status.value !in 200..299) {
        throw Exception("Error del servidor: ${call.status.value}")
    }

    return call.body()
}
