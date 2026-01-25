package uk.co.andyreed.growatt.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val result: Int,
    val obj: WeatherResponseObj? = null
)

@Serializable
data class WeatherResponseObj(
    val city: String,
    @SerialName("Week")
    val week: String,
    val dataStr: String,
    val data: WeatherData,
    val radiant: String,
    val tempType: Int
)

@Serializable
data class WeatherData(
    @SerialName("HeWeather6")
    val heWeather6: List<HeWeather6Item>
)

@Serializable
data class HeWeather6Item(
    val now: NowData,
    val update: UpdateData,
    val basic: BasicData,
    val status: String
)

@Serializable
data class NowData(
    val cloud: String,
    val hum: String,
    val wind_deg: String,
    val pres: String,
    val pcpn: String,
    val fl: String,
    val tmp: String,
    val wind_sc: String,
    val cond_txt: String,
    val wind_dir: String,
    val wind_spd: String,
    val cond_code: String
)

@Serializable
data class UpdateData(
    val loc: String,
    val utc: String
)

@Serializable
data class BasicData(
    val ss: String,
    val admin_area: String,
    val toDay: String,
    val location: String,
    val parent_city: String,
    val cnty: String,
    val sr: String
)
