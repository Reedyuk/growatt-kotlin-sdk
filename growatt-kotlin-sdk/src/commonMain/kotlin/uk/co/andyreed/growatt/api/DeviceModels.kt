package uk.co.andyreed.growatt.api

import kotlinx.serialization.Serializable


/**
 * Detailed information about a device
 */
@Serializable
data class DeviceDetail(
    val deviceId: String,
    val deviceSn: String,
    val deviceType: String,
    val deviceModel: String,
    val plantId: String,
    val plantName: String,
    val nominalPower: String,
    val location: String?,
    val timezone: String?,
    val datalogSn: String?,
    val alias: String?
)

/**
 * Real-time data from a device
 */
@Serializable
data class RealtimeData(
    val deviceId: String,
    val pac: String,           // Current power output
    val eToday: String,        // Energy today
    val eTotal: String,        // Total energy
    val lastUpdateTime: String,
    val status: String
)

/**
 * Historical data entry
 */
@Serializable
data class HistoryEntry(
    val date: String,
    val energy: String,
    val power: String?
)

/**
 * Device status information
 */
@Serializable
data class DeviceStatus(
    val deviceId: String,
    val status: String,
    val ptoStatus: String?,
    val bdcStatus: String?,
    val lastUpdateTime: String
)

/**
 * Alarm information
 */
@Serializable
data class Alarm(
    val alarmId: String,
    val deviceId: String,
    val alarmCode: String,
    val alarmMessage: String,
    val alarmType: String,
    val timestamp: String,
    val isResolved: Boolean = false
)

/**
 * Battery state information
 */
@Serializable
data class BatteryState(
    val deviceId: String,
    val soc: String,              // State of charge (%)
    val voltage: String,          // Battery voltage (V)
    val current: String,          // Battery current (A)
    val power: String,            // Battery power (W)
    val temperature: String,      // Battery temperature (°C)
    val status: String,           // Battery status (charging/discharging/idle)
    val lastUpdateTime: String
)

/**
 * Battery metrics information
 */
@Serializable
data class BatteryMetrics(
    val deviceId: String,
    val capacity: String,         // Total battery capacity (kWh)
    val remainingCapacity: String, // Remaining capacity (kWh)
    val chargedToday: String,     // Energy charged today (kWh)
    val dischargedToday: String,  // Energy discharged today (kWh)
    val chargedTotal: String,     // Total energy charged (kWh)
    val dischargedTotal: String,  // Total energy discharged (kWh)
    val cycleCount: String,       // Battery cycle count
    val health: String,           // Battery health (%)
    val lastUpdateTime: String
)

/**
 * Response wrapper for storage battery chart data
 */
@Serializable
data class StorageBatChartResponse(
    val result: Int,
    val obj: StorageBatChartData? = null
)

/**
 * Storage battery chart data
 */
@Serializable
data class StorageBatChartData(
    val date: String,
    val cdsTitle: List<String>,
    val socChart: SocChart,
    val cdsData: CdsData
)

/**
 * State of charge chart data
 */
@Serializable
data class SocChart(
    val capacity: List<Double?>
)

/**
 * Charge/discharge data
 */
@Serializable
data class CdsData(
    val cd_charge: List<Double>,
    val cd_disCharge: List<Double>
)
