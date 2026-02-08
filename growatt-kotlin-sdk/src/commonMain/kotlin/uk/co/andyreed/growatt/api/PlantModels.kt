package uk.co.andyreed.growatt.api

import kotlinx.serialization.Serializable

@Serializable
data class PlantDetailResponse(
    val result: Int,
    val obj: PlantDetail? = null
)

@Serializable
data class PlantDetail(
    val id: String? = null,
    val plantName: String? = null,
    val plantType: String? = null,
    val country: String? = null,
    val city: String? = null,
    val state: String? = null,
    val timezone: String? = null,
    val lat: String? = null,
    val lng: String? = null,
    val accountName: String? = null,
    val nominalPower: String? = null,
    val eTotal: String? = null,
    val moneyUnit: String? = null,
    val moneyUnitText: String? = null,
    val co2: String? = null,
    val coal: String? = null,
    val tree: String? = null,
    val fixedPowerPrice: String? = null,
    val valleyPeriodPrice: String? = null,
    val peakPeriodPrice: String? = null,
    val flatPeriodPrice: String? = null,
    val formulaMoney: String? = null,
    val formulaCoal: String? = null,
    val formulaCo2: String? = null,
    val formulaTree: String? = null,
    val creatDate: String? = null,
    val plantImg: String? = null,
    val isShare: String? = null,
    val tempType: String? = null,
    val gridCompany: String? = null,
    val gridPort: String? = null,
    val gridLfdi: String? = null,
    val gridServerUrl: String? = null,
    val protocolId: String? = null,
    val designCompany: String? = null,
    val locationImg: String? = null,
    val installMap: String? = null
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
