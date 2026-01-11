package uk.co.andyreed.growatt.http

import io.ktor.client.*

expect fun createHttpClient(debug: Boolean = false): HttpClient

