package uk.co.andyreed.growatt

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.engine.mock.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.http.*
import uk.co.andyreed.growatt.api.GrowattApiImpl
import uk.co.andyreed.growatt.http.createHttpClient
import kotlin.test.assertTrue

class GrowattClientTest {

    @Test
    fun login_setsToken_and_getDevices_usesIt() = runBlocking {
        val plantsJson = "[{\"timezone\":\"0\",\"id\":\"p1\",\"plantName\":\"Plant 1\"}]"

        val mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/login" -> respond(
                    content = "{\"result\":1}",
                    status = HttpStatusCode.OK
                )
                "/index/getPlantListTitle" -> {
                    respond(
                        content = plantsJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "text/html;charset=UTF-8\n")
                    )
                }
                else -> respondBadRequest()
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val api = GrowattApiImpl(client, baseUrl = "https://server.growatt.com")
        val auth = api.login("reedyuk", "password")
        assertEquals("{\"result\":1}", auth.rawBody)
        assertTrue(api.isAuthenticated)

        val plants = api.getPlantList()
        assertEquals(1, plants.size)
    }

    @Test
    fun getPlantList_returnsPlants_and_usesToken() = runBlocking {
        val plantsJson = "[{\"timezone\":\"0\",\"id\":\"p1\",\"plantName\":\"Plant 1\"}]"

        val mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/login" -> respond(
                    content = "{\"result\":1}",
                    status = HttpStatusCode.OK
                )
                "/index/getPlantListTitle" -> {
                    respond(
                        content = plantsJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "text/html;charset=UTF-8\n")
                    )
                }
                else -> respondBadRequest()
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val api = GrowattApiImpl(client, baseUrl = "https://server.growatt.com")
        val auth = api.login("reedyuk", "password")

        assertEquals("{\"result\":1}", auth.rawBody)
        val plants = api.getPlantList()
        assertEquals(1, plants.size)
        assertEquals("p1", plants[0].id)
    }

}

