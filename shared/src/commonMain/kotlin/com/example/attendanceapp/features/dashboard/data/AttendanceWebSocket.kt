package com.example.attendanceapp.features.dashboard.data

import com.example.attendanceapp.core.network.createPlatformHttpClient
import com.example.attendanceapp.core.network.TokenHolder
import com.example.attendanceapp.core.notifications.LocalNotificationManager
import com.example.attendanceapp.features.notifications.data.NotificationItem
import com.example.attendanceapp.features.notifications.data.NotificationRepository
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.wss
import io.ktor.client.request.header
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AttendanceWebSocket(
    private val notificationManager: LocalNotificationManager? = null
) {

    companion object {
        // Túnel ngrok. Requiere wss (443) — ngrok redirige el ws plano en 80.
        private const val WS_HOST = "e704-181-64-57-110.ngrok-free.app"
        private const val WS_PORT = 443
        private const val WS_PATH = "/ws"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val _attendanceUpdates = MutableSharedFlow<AttendanceData>(extraBufferCapacity = 10)
    val attendanceUpdates = _attendanceUpdates.asSharedFlow()

    private val _notifications = MutableSharedFlow<NotificationPayload>(extraBufferCapacity = 10)
    val notifications = _notifications.asSharedFlow()

    private var client = createPlatformHttpClient().config {
        install(WebSockets)
    }

    // Job de la conexión activa. Cancelar esto NO mata el HttpClient,
    // así se puede volver a llamar connect() después de un disconnect().
    private var connectionJob: Job? = null

    private var isConnected = false

    fun connect() {
        if (isConnected) return

        connectionJob = scope.launch {
            val currentToken = TokenHolder.token
            println("AttendanceWebSocket: token present=${!currentToken.isNullOrBlank()} preview=${currentToken?.take(30)}")
            try {
                client.wss(
                    host = WS_HOST,
                    port = WS_PORT,
                    path = WS_PATH,
                    request = {
                        url {
                            parameters.append("token", TokenHolder.token ?: "")
                        }
                        header("ngrok-skip-browser-warning", "true")
                    }
                ) {
                    isConnected = true
                    println("WebSocket conectado a wss://$WS_HOST:$WS_PORT$WS_PATH")

                    try {
                        while (true) {
                            val message = incoming.receive()
                            if (message is Frame.Text) {
                                val text = message.readText()
                                println("WebSocket mensaje recibido: $text")
                                try {
                                    val wsMessage = json.decodeFromString<WebSocketMessage>(text)
                                    if (wsMessage.type == "notification") {
                                        val payload = wsMessage.payload

                                        _notifications.emit(payload)

                                        val notificationItem = NotificationItem(
                                            id = payload.id,
                                            title = payload.title,
                                            message = payload.message,
                                            type = payload.type,
                                            studentId = payload.data.studentId,
                                            attendanceId = payload.data.attendanceId,
                                            attendanceTipo = payload.data.tipo,
                                            createdAt = payload.createdAt,
                                            readAt = payload.readAt
                                        )
                                        NotificationRepository.addNotification(notificationItem)

                                        notificationManager?.showNotification(
                                            title = payload.title,
                                            message = payload.message,
                                            notificationId = payload.id
                                        )

                                        if (payload.type == "attendance") {
                                            _attendanceUpdates.emit(payload.data)
                                        }
                                    }
                                } catch (e: Exception) {
                                    println("Error parseando WebSocket message: ${e.message}")
                                }
                            }
                        }
                    } finally {
                        isConnected = false
                    }
                }
            } catch (e: Exception) {
                println("WebSocket error: ${e.message}")
                isConnected = false
            }
        }
    }

    fun disconnect() {
        connectionJob?.cancel()
        connectionJob = null
        isConnected = false
    }

    // Llamar solo cuando el objeto entero deja de usarse (ej. logout definitivo,
    // cierre de la app). No confundir con disconnect(): esto sí mata el client
    // para siempre y ya no se puede volver a hacer connect() después.
    fun shutdown() {
        scope.launch {
            client.close()
        }
    }
}