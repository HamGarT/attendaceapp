package com.example.attendanceapp.features.profile.data

import com.example.attendanceapp.core.BuildConfig
import com.example.attendanceapp.core.network.authGet
import com.example.attendanceapp.core.network.createAuthHttpClient

class ProfileService {

    companion object {
        private const val BASE_URL = BuildConfig.API_URL
    }

    private val client = createAuthHttpClient()

    suspend fun getUserProfile(userId: Int): UserProfile {
        return client.authGet<UserProfile>("$BASE_URL/users/$userId")
    }

    suspend fun getMyChildren(): List<ProfileChildWrapper> {
        return client.authGet<List<ProfileChildWrapper>>("$BASE_URL/me/children")
    }
}

@kotlinx.serialization.Serializable
data class ProfileChildWrapper(
    val parentesco: String,
    val student: ProfileChild
)
