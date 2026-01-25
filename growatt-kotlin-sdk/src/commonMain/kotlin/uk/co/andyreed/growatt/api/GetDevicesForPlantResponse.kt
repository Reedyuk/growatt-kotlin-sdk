package uk.co.andyreed.growatt.api

import kotlinx.serialization.Serializable

@Serializable
data class GetDevicesForPlantResponse(
    val result: Int,
    val obj: GetDevicesForPlantResponseObj? = null
)

@Serializable
data class GetDevicesForPlantResponseObj(
    val currPage: Int,
    val pages: Int,
    val pageSize: Int,
    val count: Int,
    val ind: Int,
    val datas: List<DeviceData>,
    val notPager: Boolean
)

@Serializable
data class DeviceData(
    val deviceType: String,
    val ptoStatus: String,
    val timeServer: String,
    val accountName: String,
    val timezone: String,
    val plantId: String,
    val deviceTypeName: String,
    val bdcNum: String,
    val nominalPower: String,
    val bdcStatus: String,
    val eToday: String,
    val eMonth: String,
    val datalogTypeTest: String,
    val eTotal: String,
    val pac: String,
    val datalogSn: String,
    val alias: String,
    val location: String,
    val deviceModel: String,
    val sn: String,
    val plantName: String,
    val status: String,
    val lastUpdateTime: String
)
