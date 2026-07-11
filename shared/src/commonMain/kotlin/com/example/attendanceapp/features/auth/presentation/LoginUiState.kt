package com.example.attendanceapp.features.auth.presentation

import com.example.attendanceapp.features.auth.domain.User

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val user: User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
