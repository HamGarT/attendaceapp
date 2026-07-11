package com.example.attendanceapp.features.auth.data

/**
 * Compatibilidad temporal: el flujo actual ya usa `LoginResponse` directo,
 * así que mantenemos este helper mínimo para evitar romper compilación.
 */
fun parseLoginResponse(rawJson: String): LoginResponse {
    return LoginResponse()
}

