package com.example.attendanceapp.core.network

object AppContext {
    var storage: PersistentStorage? = null
        private set

    // Guardamos el contexto para que Android pueda usarlo
    internal var platformContext: Any? = null

    // Cambiamos `Any` a `Any?` para que iOS pueda no enviar nada
    fun init(context: Any? = null) {
        platformContext = context
        if (storage == null) {
            storage = createPersistentStorage()
        }
    }

    fun isInitialized(): Boolean = storage != null
}