package com.example.attendanceapp.core.network

import io.ktor.client.HttpClient

expect fun createPlatformHttpClient(): HttpClient
