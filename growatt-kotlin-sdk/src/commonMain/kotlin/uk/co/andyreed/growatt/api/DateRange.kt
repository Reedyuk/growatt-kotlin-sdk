package uk.co.andyreed.growatt.api

import kotlinx.serialization.Serializable

@Serializable
data class DateRange(
    val startDate: String,
    val endDate: String
)
