package com.example.attendanceapp.core.network

object AppContext {
    var storage: PersistentStorage? = null
        private set

    fun init(context: Any) {
        if (storage == null) {
            storage = createPersistentStorage()
        }
    }

    fun isInitialized(): Boolean = storage != null
}
