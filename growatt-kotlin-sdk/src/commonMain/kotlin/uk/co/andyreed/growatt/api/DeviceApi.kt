package uk.co.andyreed.growatt.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

/**
 * API interface for device-related operations
 */
interface DeviceApi {
    /**
     * Get storage battery chart data
     * @param plantId The plant identifier
     * @param storageSn The storage serial number
     * @return StorageBatChartData containing battery chart information
     */
    suspend fun getStorageBatChart(plantId: String, storageSn: String): StorageBatChartData
    
    /**
     * Get storage energy day chart data
     * @param date The date for the energy chart (format: yyyy-MM-dd)
     * @param plantId The plant identifier
     * @param storageSn The storage serial number
     * @return StorageEnergyDayChartData containing energy chart information
     */
    suspend fun getStorageEnergyDayChart(date: String, plantId: String, storageSn: String): StorageEnergyDayChartData
}

/**
 * Implementation of DeviceApi
 */
class DeviceApiImpl(
    private val client: HttpClient,
    private val baseUrl: String = "https://server.growatt.com"
) : DeviceApi {

    private val json = Json { ignoreUnknownKeys = true }
    override suspend fun getStorageBatChart(plantId: String, storageSn: String): StorageBatChartData {
        val response = client.post("$baseUrl/panel/storage/getStorageBatChart") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("plantId", plantId)
                        append("storageSn", storageSn)
                    }
                )
            )
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        val chartResponse = json.decodeFromString<StorageBatChartResponse>(bodyAsText)
        return chartResponse.obj ?: throw ApiException(chartResponse.result, "Failed to get storage battery chart")
    }

    override suspend fun getStorageEnergyDayChart(date: String, plantId: String, storageSn: String): StorageEnergyDayChartData {
        val response = client.post("$baseUrl/panel/storage/getStorageEnergyDayChart") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("date", date)
                        append("plantId", plantId)
                        append("storageSn", storageSn)
                    }
                )
            )
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        val chartResponse = json.decodeFromString<StorageEnergyDayChartResponse>(bodyAsText)
        return chartResponse.obj ?: throw ApiException(chartResponse.result, "Failed to get storage energy day chart")
    }
}
