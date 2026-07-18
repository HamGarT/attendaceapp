package com.example.attendanceapp.features.profile.domain

import com.example.attendanceapp.features.profile.data.ProfileChildWrapper
import com.example.attendanceapp.features.profile.data.UserProfile

interface ProfileRepository {
    suspend fun getUserProfile(userId: Int): Result<UserProfile>
    suspend fun getMyChildren(): Result<List<ProfileChildWrapper>>
}