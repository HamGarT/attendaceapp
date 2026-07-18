package com.example.attendanceapp.core.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        runBlocking { _tokenFlow.emit(token) }
        // TODO: send the new token to your Express backend here too
    }

    override fun onMessageReceived(message: RemoteMessage) {
        runBlocking { _messageFlow.emit(message) }
    }

    companion object {
        private val _tokenFlow = MutableSharedFlow<String>(replay = 1)
        val tokenFlow = _tokenFlow.asSharedFlow()

        private val _messageFlow = MutableSharedFlow<RemoteMessage>()
        val messageFlow = _messageFlow.asSharedFlow()
    }
}