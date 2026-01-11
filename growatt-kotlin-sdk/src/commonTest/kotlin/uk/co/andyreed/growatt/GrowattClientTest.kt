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

class GrowattClientTest {

    @Test
    fun getDevices_returnsList() = runBlocking {
        val devicesJson = "[{\"id\":\"d1\",\"name\":\"Device 1\",\"status\":\"online\"}]"
        val mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/devices" -> respond(
                    content = devicesJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
                else -> respondBadRequest()
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        val api = GrowattApiImpl(client, baseUrl = "https://server.growatt.com")
        val list = api.getDevices()
        assertEquals(1, list.size)
        assertEquals("d1", list[0].id)
    }
}

