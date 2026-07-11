package com.example.attendanceapp.core.network

import android.content.Context
import android.content.SharedPreferences

actual class PersistentStorage private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "lumina_auth",
        Context.MODE_PRIVATE
    )

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

    companion object {
        private var instance: PersistentStorage? = null

        fun init(context: Context) {
            if (instance == null) {
                instance = PersistentStorage(context.applicationContext)
            }
        }

        fun getInstance(): PersistentStorage {
            return instance ?: throw IllegalStateException("PersistentStorage not initialized. Call init(context) first.")
        }
    }
}

actual fun createPersistentStorage(): PersistentStorage = PersistentStorage.getInstance()
