package com.example.attendanceapp.core.network

import android.content.Context
import android.content.SharedPreferences

actual class PersistentStorage {

    // Obtenemos el contexto que guardamos en AppContext.init()
    private val prefs: SharedPreferences by lazy {
        val context = AppContext.platformContext as? Context
            ?: throw IllegalStateException("Context no inicializado. Llama a AppContext.init(context) en tu MainActivity")
        context.getSharedPreferences("attendance_app_prefs", Context.MODE_PRIVATE)
    }

    actual fun save(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun get(key: String): String? {
        return prefs.getString(key, null)
    }

    actual fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    actual fun clear() {
        prefs.edit().clear().apply()
    }
}

actual fun createPersistentStorage(): PersistentStorage = PersistentStorage()