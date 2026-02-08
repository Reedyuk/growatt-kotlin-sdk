package uk.co.andyreed.growatt.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.json.Json

interface PlantApi {
    suspend fun getPlantDetail(plantId: String): PlantDetail
    suspend fun getPlantEnergyToday(plantId: String): EnergySummary
    suspend fun getPlantEnergyHistory(plantId: String, range: DateRange): List<EnergyData>
    suspend fun getPlantDevices(plantId: String): List<Device>
}

class PlantApiImpl(
    private val client: HttpClient,
    private val baseUrl: String = "https://server.growatt.com"
) : PlantApi {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getPlantDetail(plantId: String): PlantDetail {
        val response = client.post("$baseUrl/panel/getPlantData") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("plantId", plantId)
                    }
                )
            )
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        println("getPlantDetail Body: $bodyAsText")
        val plantDetailResponse = json.decodeFromString<PlantDetailResponse>(bodyAsText)
        return plantDetailResponse.obj ?: throw ApiException(plantDetailResponse.result, "Failed to get plant detail")
    }

    override suspend fun getPlantEnergyToday(plantId: String): EnergySummary {
        val response = client.post("$baseUrl/panel/getPlantData") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("plantId", plantId)
                    }
                )
            )
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        println("getPlantEnergyToday Body: $bodyAsText")
        val plantDetailResponse = json.decodeFromString<PlantDetailResponse>(bodyAsText)
        val plantDetail = plantDetailResponse.obj ?: throw ApiException(plantDetailResponse.result, "Failed to get plant detail")
        // Note: The API doesn't return todayEnergy in this endpoint, using eTotal as total energy
        return EnergySummary(
            todayEnergy = 0.0, // This endpoint doesn't provide todayEnergy
            todayIncome = 0.0  // This endpoint doesn't provide todayIncome
        )
    }

    override suspend fun getPlantEnergyHistory(plantId: String, range: DateRange): List<EnergyData> {
        val response = client.post("$baseUrl/panel/getPlantEnergyData") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("plantId", plantId)
                        append("startDate", range.startDate)
                        append("endDate", range.endDate)
                    }
                )
            )
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        println("getPlantEnergyHistory Body: $bodyAsText")
        return json.decodeFromString<List<EnergyData>>(bodyAsText)
    }

    override suspend fun getPlantDevices(plantId: String): List<Device> {
        val response = client.post("$baseUrl/panel/getDevicesByPlantList") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("plantId", plantId)
                        append("currPage", "1")
                    }
                )
            )
            header(HttpHeaders.Accept, "*/*")
        }
        val bodyAsText = response.bodyAsText()
        println("getPlantDevices Body: $bodyAsText")
        val devicesResponse = json.decodeFromString<GetDevicesForPlantResponse>(bodyAsText)
        return devicesResponse.obj?.datas?.map {
            Device(
                id = it.sn,
                name = it.alias,
                status = it.status
            )
        } ?: emptyList()
    }
}
