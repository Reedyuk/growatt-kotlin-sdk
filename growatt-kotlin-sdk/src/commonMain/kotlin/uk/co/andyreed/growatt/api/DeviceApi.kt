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
    
    /**
     * Get real-time snapshot of all system data (battery, solar, grid, consumption)
     * Combines data from multiple endpoints to provide current power flows
     * @param date The date for the snapshot (format: yyyy-MM-dd, typically today's date)
     * @param plantId The plant identifier
     * @param storageSn The storage serial number
     * @return RealtimeSnapshot containing current solar, battery, grid, and consumption data
     */
    suspend fun getRealtimeSnapshot(date: String, plantId: String, storageSn: String): RealtimeSnapshot
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

    override suspend fun getRealtimeSnapshot(date: String, plantId: String, storageSn: String): RealtimeSnapshot {
        // Fetch both battery chart and energy chart data
        val batteryChart = getStorageBatChart(plantId, storageSn)
        val energyChart = getStorageEnergyDayChart(date, plantId, storageSn)
        
        // Get the latest (most recent non-null) values from the time-series data
        val latestSolarPower = energyChart.charts.ppv.lastNotNull()
        val latestPowerToGrid = energyChart.charts.pacToGrid.lastNotNull()
        val latestUserLoad = energyChart.charts.userLoad.lastNotNull()
        val latestPowerToUser = energyChart.charts.pacToUser.lastNotNull()
        val latestSoc = batteryChart.socChart.capacity.lastNotNull()
        
        // Calculate battery power (positive = charging, negative = discharging)
        // If power is going to grid and solar > load, battery might be charging
        // This is an approximation - actual battery power would need a dedicated endpoint
        val batteryPower = when {
            latestPowerToUser != null && latestPowerToUser > 0.0 -> -latestPowerToUser // discharging to user
            latestPowerToGrid != null && latestSolarPower != null && latestUserLoad != null -> {
                val excessSolar = latestSolarPower - latestUserLoad
                if (excessSolar > latestPowerToGrid) excessSolar - latestPowerToGrid else null // charging from excess solar
            }
            else -> null
        }
        
        val batteryStatus = when {
            batteryPower == null -> "idle"
            batteryPower > 0 -> "charging"
            batteryPower < 0 -> "discharging"
            else -> "idle"
        }
        
        // Calculate grid import (negative values) vs export (positive values)
        val netGridPower = latestPowerToGrid ?: 0.0
        val powerFromGrid = if (netGridPower < 0) -netGridPower else null
        val powerToGrid = if (netGridPower > 0) netGridPower else null
        
        return RealtimeSnapshot(
            timestamp = date,
            solar = SolarData(
                currentPower = latestSolarPower,
                energyToday = energyChart.eCharge,
                energyTotal = energyChart.eChargeTotal
            ),
            battery = BatteryData(
                stateOfCharge = latestSoc,
                power = batteryPower,
                status = batteryStatus,
                chargedToday = energyChart.eCharge,
                dischargedToday = energyChart.eDisCharge
            ),
            grid = GridData(
                powerToGrid = powerToGrid,
                powerFromGrid = powerFromGrid,
                netPower = netGridPower
            ),
            consumption = ConsumptionData(
                currentLoad = latestUserLoad,
                powerFromSolar = latestSolarPower,
                powerFromBattery = if (batteryPower != null && batteryPower < 0) -batteryPower else null
            )
        )
    }
    
    /**
     * Extension function to get the last non-null value from a list
     */
    private fun List<Double?>.lastNotNull(): Double? = this.lastOrNull { it != null }
}
