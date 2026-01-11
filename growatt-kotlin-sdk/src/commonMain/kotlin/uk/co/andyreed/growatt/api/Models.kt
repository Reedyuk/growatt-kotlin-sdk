package uk.co.andyreed.growatt.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Device(val id: String, val name: String, val status: String, val extra: JsonObject? = null)

class ApiException(val code: Int, message: String) : Exception(message)
