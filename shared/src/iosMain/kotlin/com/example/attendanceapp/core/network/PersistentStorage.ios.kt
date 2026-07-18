package com.example.attendanceapp.core.network

import platform.Foundation.NSUserDefaults


actual class PersistentStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual fun save(key: String, value: String) {
        userDefaults.setObject(value, forKey = key)
    }

    actual fun get(key: String): String? {
        return userDefaults.stringForKey(key)
    }

    actual fun remove(key: String) {
        userDefaults.removeObjectForKey(key)
    }

    actual fun clear() {
        val domain = platform.Foundation.NSBundle.mainBundle.bundleIdentifier
        if (domain != null) {
            userDefaults.removePersistentDomainForName(domain)
        }
    }
}

actual fun createPersistentStorage(): PersistentStorage = PersistentStorage()