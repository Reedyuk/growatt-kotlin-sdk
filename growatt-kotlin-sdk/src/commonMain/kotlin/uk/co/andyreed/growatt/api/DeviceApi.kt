package uk.co.andyreed.growatt.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

/**
 * API interface for device-related operations
 */
interface DeviceApi {
    /**
     * Get detailed information about a device
     * @param deviceId The unique identifier of the device
     * @return DeviceDetail containing detailed device information
     */
    suspend fun getDetail(deviceId: String): DeviceDetail
    
    /**
     * Get real-time data from a device
     * @param deviceId The unique identifier of the device
     * @return RealtimeData containing current device metrics
     */
    suspend fun getRealtime(deviceId: String): RealtimeData
    
    /**
     * Get historical data for a device
     * @param deviceId The unique identifier of the device
     * @param range The date range for the history query
     * @return List of HistoryEntry records
     */
    suspend fun getHistory(deviceId: String, range: DateRange): List<HistoryEntry>
    
    /**
     * Get the current status of a device
     * @param deviceId The unique identifier of the device
     * @return DeviceStatus containing status information
     */
    suspend fun getStatus(deviceId: String): DeviceStatus
    
    /**
     * Get alarms for a device
     * @param deviceId The unique identifier of the device
     * @return List of Alarm records
     */
    suspend fun getAlarms(deviceId: String): List<Alarm>
    
    /**
     * Get the current battery state of a device
     * @param deviceId The unique identifier of the device
     * @return BatteryState containing current battery state information
     */
    suspend fun getBatteryState(deviceId: String): BatteryState
    
    /**
     * Get battery metrics for a device
     * @param deviceId The unique identifier of the device
     * @return BatteryMetrics containing battery performance metrics
     */
    suspend fun getBatteryMetrics(deviceId: String): BatteryMetrics
}

/**
 * Implementation of DeviceApi
 */
class DeviceApiImpl(
    private val client: HttpClient,
    private val baseUrl: String = "https://server.growatt.com"
) : DeviceApi {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getDetail(deviceId: String): DeviceDetail {
        val response = client.get("$baseUrl/panel/getDeviceDetail") {
            parameter("deviceSn", deviceId)
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        return json.decodeFromString<DeviceDetail>(bodyAsText)
    }

    override suspend fun getRealtime(deviceId: String): RealtimeData {
        val response = client.get("$baseUrl/panel/getDeviceRealtime") {
            parameter("deviceSn", deviceId)
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        return json.decodeFromString<RealtimeData>(bodyAsText)
    }

    override suspend fun getHistory(deviceId: String, range: DateRange): List<HistoryEntry> {
        val response = client.get("$baseUrl/panel/getDeviceHistory") {
            parameter("deviceSn", deviceId)
            parameter("startDate", range.startDate)
            parameter("endDate", range.endDate)
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        return json.decodeFromString<List<HistoryEntry>>(bodyAsText)
    }

    override suspend fun getStatus(deviceId: String): DeviceStatus {
        val response = client.get("$baseUrl/panel/getDeviceStatus") {
            parameter("deviceSn", deviceId)
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        return json.decodeFromString<DeviceStatus>(bodyAsText)
    }

    override suspend fun getAlarms(deviceId: String): List<Alarm> {
        val response = client.get("$baseUrl/panel/getDeviceAlarms") {
            parameter("deviceSn", deviceId)
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        return json.decodeFromString<List<Alarm>>(bodyAsText)
    }

    override suspend fun getBatteryState(deviceId: String): BatteryState {
        val response = client.get("$baseUrl/panel/getBatteryState") {
            parameter("deviceSn", deviceId)
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        return json.decodeFromString<BatteryState>(bodyAsText)
    }

    override suspend fun getBatteryMetrics(deviceId: String): BatteryMetrics {
        val response = client.get("$baseUrl/panel/getBatteryMetrics") {
            parameter("deviceSn", deviceId)
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        return json.decodeFromString<BatteryMetrics>(bodyAsText)
    }
}
