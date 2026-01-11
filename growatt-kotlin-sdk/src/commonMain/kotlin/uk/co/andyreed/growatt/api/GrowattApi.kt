package uk.co.andyreed.growatt.api

import io.ktor.client.*

interface GrowattApi {
    suspend fun login(username: String, password: String): AuthResponse
    suspend fun getDevices(token: String): List<Device>
}

