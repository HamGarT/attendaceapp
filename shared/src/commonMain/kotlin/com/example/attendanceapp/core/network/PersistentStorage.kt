package com.example.attendanceapp.core.network

expect class PersistentStorage {
    fun save(key: String, value: String)
    fun get(key: String): String?
    fun remove(key: String)
    fun clear()
}

expect fun createPersistentStorage(): PersistentStorage
