package com.example.attendanceapp.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.features.auth.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, rememberMe: Boolean = true) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Ingresa email y contrasena")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = repository.login(email, password, rememberMe)
            _uiState.value = result.fold(
                onSuccess = { LoginUiState.Success(it) },
                onFailure = { LoginUiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
