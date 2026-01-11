package uk.co.andyreed.growatt.api

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

interface GrowattApi {
    /** Mutable token that callers can set or that implementations may populate after login. */
    var token: String?

    /** Convenience: use the client's stored token. Throws if token is not set. */
    suspend fun getDevices(): List<Device>
}

class GrowattApiImpl(
    private val client: HttpClient,
    private val baseUrl: String
) : GrowattApi {

    override var token: String? = null

    override suspend fun getDevices(): List<Device> =
        client.get("$baseUrl/devices") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
}
