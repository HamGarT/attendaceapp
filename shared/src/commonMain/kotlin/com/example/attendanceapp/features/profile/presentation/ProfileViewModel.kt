package com.example.attendanceapp.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.core.network.TokenHolder
import com.example.attendanceapp.features.profile.data.ProfileChildWrapper
import com.example.attendanceapp.features.profile.data.ProfileRepositoryImpl
import com.example.attendanceapp.features.profile.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepositoryImpl = ProfileRepositoryImpl()
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _children = MutableStateFlow<List<ProfileChildWrapper>>(emptyList())
    val children: StateFlow<List<ProfileChildWrapper>> = _children.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val userName: String get() = TokenHolder.userName
    val userLastname: String get() = TokenHolder.userLastname
    val userEmail: String get() = TokenHolder.userEmail
    val userRole: String get() = TokenHolder.userRole

    init {
        fetchProfile()
        fetchChildren()
    }

    private fun fetchProfile() {
        val userId = TokenHolder.userId
        if (userId == 0) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getUserProfile(userId)
            result.fold(
                onSuccess = { userProfile ->
                    _profile.value = userProfile
                    _error.value = null
                },
                onFailure = { e ->
                    _error.value = e.message ?: "Error al cargar perfil"
                }
            )
            _isLoading.value = false
        }
    }

    private fun fetchChildren() {
        viewModelScope.launch {
            val result = repository.getMyChildren()
            result.fold(
                onSuccess = { childrenList ->
                    _children.value = childrenList
                },
                onFailure = { e ->
                    println("ProfileViewModel: Error fetching children: ${e.message}")
                }
            )
        }
    }
}
