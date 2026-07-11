package com.example.attendanceapp.features.dashboard.data

import com.example.attendanceapp.core.BuildConfig
import com.example.attendanceapp.core.network.authGet
import com.example.attendanceapp.core.network.createAuthHttpClient

class ChildrenService {

    companion object {
        private const val BASE_URL = BuildConfig.API_URL
    }

    private val client = createAuthHttpClient()

    suspend fun getChildren(): List<ParentChild> {
        println("ChildrenService: Fetching children from $BASE_URL/me/children")
        return try {
            val result = client.authGet<List<ParentChild>>("$BASE_URL/me/children")
            println("ChildrenService: Got ${result.size} children")
            result
        } catch (e: Exception) {
            println("ChildrenService ERROR: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}
