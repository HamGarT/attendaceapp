package com.example.attendanceapp.features.scanner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.features.scanner.data.AttendanceRepositoryImpl
import com.example.attendanceapp.features.scanner.domain.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScannerUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
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
                successMessage = null,
                lastScannedId = studentId
            )

            val result = attendanceRepository.registerAttendance(studentId)

            result.onSuccess { message ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = message,
                    errorMessage = null
                )
                println("Scanner: Asistencia registrada - Estudiante $studentId")
            }

            result.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Error desconocido",
                    successMessage = null
                )
                println("Scanner: Error al registrar asistencia - ${exception.message}")
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }
}

