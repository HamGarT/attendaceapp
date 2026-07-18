package com.example.attendanceapp.features.auth.data

import com.example.attendanceapp.core.network.TokenHolder
import com.example.attendanceapp.features.auth.domain.AuthRepository
import com.example.attendanceapp.features.auth.domain.User

class AuthRepositoryImpl(
    private val authService: AuthService = AuthService()
) : AuthRepository {

    override suspend fun login(email: String, password: String, rememberMe: Boolean): Result<User> {
        return try {
            val response = authService.login(email, password, rememberMe)
            if (response.token != null && response.user != null) {
                TokenHolder.saveTokens(response.token, response.refreshToken)

                val user = User(
                    id = response.user.id ?: 0,
                    name = response.user.name ?: "",
                    lastname = response.user.lastname ?: "",
                    email = response.user.email ?: email,
                    role = response.user.roles.firstOrNull() ?: "",
                    token = response.token
                )

                TokenHolder.saveUserInfo(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    role = user.role,
                    lastname = user.lastname
                )

                println("AuthRepository: Login successful, tokens saved")
                Result.success(user)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
