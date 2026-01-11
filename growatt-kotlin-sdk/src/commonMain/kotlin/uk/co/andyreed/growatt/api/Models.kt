package uk.co.andyreed.growatt.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class AuthRequest(val username: String, val password: String)

@Serializable
data class AuthResponse(val token: String, val expiresIn: Long? = null)

@Serializable
data class Device(val id: String, val name: String, val status: String, val extra: JsonObject? = null)

class ApiException(val code: Int, message: String) : Exception(message)

