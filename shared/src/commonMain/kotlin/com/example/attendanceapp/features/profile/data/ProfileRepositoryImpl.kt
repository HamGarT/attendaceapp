package com.example.attendanceapp.features.profile.data

import com.example.attendanceapp.features.profile.domain.ProfileRepository

class ProfileRepositoryImpl(
    private val profileService: ProfileService = ProfileService()
) : ProfileRepository {

    override suspend fun getUserProfile(userId: Int): Result<UserProfile> {
        return try {
            val profile = profileService.getUserProfile(userId)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyChildren(): Result<List<ProfileChildWrapper>> {
        return try {
            val children = profileService.getMyChildren()
            Result.success(children)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}