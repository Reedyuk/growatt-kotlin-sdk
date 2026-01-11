package uk.co.andyreed.growatt.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

class GrowattClient(
    private val client: HttpClient,
    private val baseUrl: String = "https://server.growatt.com"
) : GrowattApi {

    override suspend fun login(username: String, password: String): AuthResponse {
        val req = AuthRequest(username = username, password = password)
        val response: HttpResponse = client.post("$baseUrl/login") {
            contentType(ContentType.Application.Json)
            setBody(req)
        }
        if (!response.status.isSuccess()) {
            throw ApiException(response.status.value, "Login failed: ${response.status}")
        }
        return response.body()
    }

    override suspend fun getDevices(token: String): List<Device> {
        val response: HttpResponse = client.get("$baseUrl/devices") {
            header(HttpHeaders.Authorization, "Bearer $token")
            accept(ContentType.Application.Json)
        }
        if (!response.status.isSuccess()) {
            throw ApiException(response.status.value, "Get devices failed: ${response.status}")
        }
        return response.body()
    }
}

