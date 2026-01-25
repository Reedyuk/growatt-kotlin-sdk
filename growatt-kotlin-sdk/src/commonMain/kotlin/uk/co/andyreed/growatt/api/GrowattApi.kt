package uk.co.andyreed.growatt.api

import io.ktor.client.*
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.*
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.json.Json

interface GrowattApi {
    /** Mutable token that callers can set or that implementations may populate after login. */
    var isAuthenticated: Boolean
    suspend fun login(username: String, password: String): AuthResponse
    suspend fun getPlantList(): List<Plant>
}

class GrowattApiImpl(
    private val client: HttpClient,
    private val baseUrl: String = "https://server.growatt.com"
) : GrowattApi {

    override var isAuthenticated: Boolean = false

    override suspend fun login(username: String, password: String): AuthResponse {
        val response: HttpResponse = client.post("$baseUrl/login") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("account", username)
                        append("password", password)
                        append("validateCode", "")
                        append("isRemember", "0")
                    }
                )
            )
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            header(HttpHeaders.Accept, "*/*")
        }

        val bodyText = response.bodyAsText()
        isAuthenticated = bodyText == "{\"result\":1}"
        return AuthResponse(bodyText, isAuthenticated)
    }

//    https://server.growatt.com/index/getPlantListTitle
    override suspend fun getPlantList(): List<Plant> {
        val response = client.get("$baseUrl/index/getPlantListTitle") {
            contentType(ContentType.Application.FormUrlEncoded)
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        println("Plant List Body: $bodyAsText")
        val plants: List<Plant> = Json.decodeFromString(bodyAsText)
        return plants
    }

}
