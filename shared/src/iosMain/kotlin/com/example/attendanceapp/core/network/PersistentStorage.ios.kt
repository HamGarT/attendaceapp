package com.example.attendanceapp.core.network

import platform.Foundation.NSUserDefaults

actual class PersistentStorage {
    private val defaults = NSUserDefaults()

    actual fun save(key: String, value: String) {
        defaults.setObject(value, key)
    }

    actual fun get(key: String): String? {
        return defaults.stringForKey(key)
    }

    actual fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }

    actual fun clear() {
        defaults.removeObjectForKey("access_token")
        defaults.removeObjectForKey("refresh_token")
    }
}

actual fun createPersistentStorage(): PersistentStorage = PersistentStorage()
