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

/**
 * Response wrapper for storage energy day chart data
 */
@Serializable
data class StorageEnergyDayChartResponse(
    val result: Int,
    val obj: StorageEnergyDayChartData? = null
)

/**
 * Storage energy day chart data
 */
@Serializable
data class StorageEnergyDayChartData(
    val eChargeTotal: String,
    val charts: EnergyCharts,
    val dtc: Int,
    val eAcDisCharge: String,
    val eDisCharge: String,
    val eCharge: String,
    val eAcCharge: String,
    val eDisChargeTotal: String
)

/**
 * Energy charts containing various power and load data
 */
@Serializable
data class EnergyCharts(
    val pacToGrid: List<Double?>,
    val ppv: List<Double?>,
    val sysOut: List<Double?>,
    val userLoad: List<Double?>,
    val pacToUser: List<Double?>
)

/**
 * Real-time snapshot of all system data (battery, solar, grid)
 */
@Serializable
data class RealtimeSnapshot(
    val timestamp: String,
    val solar: SolarData,
    val battery: BatteryData,
    val grid: GridData,
    val consumption: ConsumptionData
)

/**
 * Real-time solar data
 */
@Serializable
data class SolarData(
    val currentPower: Double?,      // Current solar power generation (W)
    val energyToday: String,        // Energy generated today (kWh)
    val energyTotal: String         // Total energy generated (kWh)
)

/**
 * Real-time battery data
 */
@Serializable
data class BatteryData(
    val stateOfCharge: Double?,     // Battery level (%)
    val power: Double?,             // Current battery power (W) - positive = charging, negative = discharging
    val status: String,             // charging/discharging/idle
    val chargedToday: String,       // Energy charged today (kWh)
    val dischargedToday: String     // Energy discharged today (kWh)
)

/**
 * Real-time grid data
 */
@Serializable
data class GridData(
    val powerToGrid: Double?,       // Power being exported to grid (W)
    val powerFromGrid: Double?,     // Power being imported from grid (W)
    val netPower: Double?           // Net power (positive = exporting, negative = importing)
)

/**
 * Real-time consumption data
 */
@Serializable
data class ConsumptionData(
    val currentLoad: Double?,       // Current power consumption (W)
    val powerFromSolar: Double?,    // Power from solar to load (W)
    val powerFromBattery: Double?   // Power from battery to load (W)
)
