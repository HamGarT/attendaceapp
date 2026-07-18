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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AttendanceWebSocket(
    private val notificationManager: LocalNotificationManager? = null
) {

    companion object {
        private const val WS_HOST = "192.168.100.6:3002"
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

    private var connectionJob: Job? = null
    private var isConnected = false

    fun connect() {
        // Evitamos lanzar múltiples intentos si ya estamos intentando conectar
        if (connectionJob?.isActive == true) return

        connectionJob = scope.launch {
            var currentDelay = 1000L // Empezamos esperando 1 segundo tras un fallo
            val maxDelay = 30000L // Máximo 30 segundos de espera entre intentos

            // Este bucle infinito es la magia de la Reconexión Automática
            while (isActive) {
                val currentToken = TokenHolder.token
                println("AttendanceWebSocket: Intentando conectar... token present=${!currentToken.isNullOrBlank()}")

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
                        currentDelay = 1000L // ¡Conexión exitosa! Reseteamos el contador de espera a 1 segundo
                        println("WebSocket conectado a wss://$WS_HOST:$WS_PORT$WS_PATH")

                        try {
                            // Mantenemos el socket vivo y escuchando
                            while (isActive) {
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
                            println("WebSocket: Bucle de recepción cerrado.")
                        }
                    }
                } catch (e: Exception) {
                    // Si el error es porque llamamos a disconnect() manualmente, rompemos el bucle
                    if (e is CancellationException) throw e

                    isConnected = false
                    println("WebSocket desconectado o error de red: ${e.message}")
                }

                // --- RETROCESO EXPONENCIAL (EXPONENTIAL BACKOFF) ---
                // Si llegamos aquí, es porque el socket se cayó o no hubo internet al intentar conectar.
                println("WebSocket: Intentando reconectar en ${currentDelay / 1000} segundos...")
                delay(currentDelay)
                // Multiplicamos el tiempo de espera por 2 (1s -> 2s -> 4s -> 8s -> 16s -> 30s)
                currentDelay = (currentDelay * 2).coerceAtMost(maxDelay)
            }
        }
    }

    fun disconnect() {
        println("AttendanceWebSocket: Desconexión manual solicitada.")
        connectionJob?.cancel() // Esto lanza CancellationException y rompe el bucle while(isActive)
        connectionJob = null
        isConnected = false
    }

    fun shutdown() {
        scope.launch {
            client.close()
        }
    }
}