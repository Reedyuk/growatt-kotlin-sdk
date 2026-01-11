package uk.co.andyreed.growatt.api

import kotlinx.serialization.Serializable

@Serializable
data class Plant(
    val id: String,
    val plantName: String,
    val timezone: Int
)
