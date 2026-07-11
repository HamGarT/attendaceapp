package com.example.attendanceapp.features.scanner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.features.scanner.data.AttendanceRepositoryImpl
import com.example.attendanceapp.features.scanner.data.AttendanceResponse
import com.example.attendanceapp.features.scanner.domain.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScannerUiState(
    val isLoading: Boolean = false,
    val scannedData: AttendanceResponse? = null,
    val errorMessage: String? = null,
    val lastScannedId: Int? = null
)

class ScannerViewModel(
    private val attendanceRepository: AttendanceRepository = AttendanceRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    fun registerAttendance(studentId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                scannedData = null,
                lastScannedId = studentId
            )

            val result = attendanceRepository.registerAttendance(studentId)

            result.onSuccess { responseData ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    scannedData = responseData, // ✨ Guardamos los datos reales aquí
                    errorMessage = null
                )
            }

            result.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Error desconocido",
                    scannedData = null
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            scannedData = null, // ✨ Limpiamos los datos para escanear de nuevo
            errorMessage = null
        )
    }

    fun showError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = message,
            scannedData = null
        )
    }
}

