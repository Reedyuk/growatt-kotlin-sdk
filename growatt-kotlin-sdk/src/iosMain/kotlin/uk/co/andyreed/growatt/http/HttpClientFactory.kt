package uk.co.andyreed.growatt.http

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.*
import kotlinx.serialization.json.Json

actual fun createHttpClient(debug: Boolean): HttpClient = HttpClient(Darwin) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true; isLenient = true })
    }
    install(Logging) {
        logger = Logger.SIMPLE
        level = if (debug) LogLevel.ALL else LogLevel.INFO
    }
    install(HttpCookies)
    install(HttpTimeout) {
        requestTimeoutMillis = 60_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 60_000
    }
    defaultRequest {
        header(HttpHeaders.Accept, ContentType.Application.Json)
        header(HttpHeaders.UserAgent, "Growatt-Kotlin-SDK/1.0")
    }
}

