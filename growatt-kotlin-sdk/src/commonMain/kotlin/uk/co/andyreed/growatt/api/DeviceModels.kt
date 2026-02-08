package uk.co.andyreed.growatt.api

import kotlinx.serialization.Serializable

/**
 * Date range for querying device history
 */
@Serializable
data class DateRange(
    val startDate: String, // ISO 8601 format: YYYY-MM-DD
    val endDate: String    // ISO 8601 format: YYYY-MM-DD
)

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
