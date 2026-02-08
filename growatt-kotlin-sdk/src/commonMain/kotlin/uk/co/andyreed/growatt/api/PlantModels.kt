package uk.co.andyreed.growatt.api

import kotlinx.serialization.Serializable

@Serializable
data class PlantDetail(
    val plantId: String? = null,
    val plantName: String? = null,
    val currentPower: Double? = null,
    val todayEnergy: Double? = null,
    val totalEnergy: Double? = null,
    val todayIncome: Double? = null,
    val totalIncome: Double? = null,
    val status: String? = null,
    val timezone: Int? = null,
    val city: String? = null,
    val country: String? = null
)

@Serializable
data class EnergySummary(
    val todayEnergy: Double,
    val todayIncome: Double
)

@Serializable
data class DateRange(
    val startDate: String,
    val endDate: String
)

@Serializable
data class EnergyData(
    val date: String? = null,
    val energy: Double? = null,
    val income: Double? = null
)
