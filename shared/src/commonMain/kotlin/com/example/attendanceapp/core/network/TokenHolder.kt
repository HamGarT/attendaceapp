package com.example.attendanceapp.core.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TokenHolder {
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_USER_ID = "user_id"

    var token: String? = null
        private set

    var refreshToken: String? = null
        private set

    var userName: String = ""
        private set

    var userEmail: String = ""
        private set

    var userRole: String = ""
        private set

    var userId: Int = 0
        private set

    private val _sessionActive = MutableStateFlow(false)
    val sessionActive: StateFlow<Boolean> = _sessionActive.asStateFlow()

    fun saveTokens(access: String, refresh: String?) {
        token = access
        refreshToken = refresh
        AppContext.storage?.save(KEY_ACCESS_TOKEN, access)
        refresh?.let { AppContext.storage?.save(KEY_REFRESH_TOKEN, it) }
        _sessionActive.value = hasValidSession()
    }

    fun saveUserInfo(id: Int, name: String, email: String, role: String) {
        userId = id
        userName = name
        userEmail = email
        userRole = role
        AppContext.storage?.save(KEY_USER_ID, id.toString())
        AppContext.storage?.save(KEY_USER_NAME, name)
        AppContext.storage?.save(KEY_USER_EMAIL, email)
        AppContext.storage?.save(KEY_USER_ROLE, role)
    }

    fun loadFromStorage() {
        val storage = AppContext.storage ?: return
        token = storage.get(KEY_ACCESS_TOKEN)
        refreshToken = storage.get(KEY_REFRESH_TOKEN)
        userName = storage.get(KEY_USER_NAME) ?: ""
        userEmail = storage.get(KEY_USER_EMAIL) ?: ""
        userRole = storage.get(KEY_USER_ROLE) ?: ""
        userId = storage.get(KEY_USER_ID)?.toIntOrNull() ?: 0
        _sessionActive.value = hasValidSession()
    }

    fun clear() {
        token = null
        refreshToken = null
        userName = ""
        userEmail = ""
        userRole = ""
        userId = 0
        AppContext.storage?.clear()
        _sessionActive.value = false
    }

    fun hasValidSession(): Boolean {
        return !token.isNullOrBlank() && !refreshToken.isNullOrBlank()
    }

    fun getBearerToken(): String? = token?.let { "Bearer $it" }
}
