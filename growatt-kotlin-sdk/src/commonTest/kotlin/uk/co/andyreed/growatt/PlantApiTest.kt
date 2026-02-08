package uk.co.andyreed.growatt

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import uk.co.andyreed.growatt.api.DateRange
import uk.co.andyreed.growatt.api.PlantApiImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlantApiTest {

    @Test
    fun getPlantDetail_returnsPlantDetail() = runBlocking {
        val plantDetailJson = """
            {
                "plantId": "123",
                "plantName": "Test Plant",
                "currentPower": 5.5,
                "todayEnergy": 25.3,
                "totalEnergy": 1500.0,
                "todayIncome": 3.5,
                "totalIncome": 200.0,
                "status": "1",
                "timezone": 0,
                "city": "London",
                "country": "UK"
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/panel/getPlantData" -> respond(
                    content = plantDetailJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
                else -> respondBadRequest()
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val api = PlantApiImpl(client, baseUrl = "https://server.growatt.com")
        val plantDetail = api.getPlantDetail("123")
        
        assertEquals("123", plantDetail.plantId)
        assertEquals("Test Plant", plantDetail.plantName)
        assertEquals(5.5, plantDetail.currentPower)
        assertEquals(25.3, plantDetail.todayEnergy)
        assertEquals(1500.0, plantDetail.totalEnergy)
    }

    @Test
    fun getPlantEnergyToday_returnsEnergySummary() = runBlocking {
        val plantDetailJson = """
            {
                "plantId": "123",
                "plantName": "Test Plant",
                "todayEnergy": 25.3,
                "todayIncome": 3.5
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/panel/getPlantData" -> respond(
                    content = plantDetailJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
                else -> respondBadRequest()
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val api = PlantApiImpl(client, baseUrl = "https://server.growatt.com")
        val energySummary = api.getPlantEnergyToday("123")
        
        assertEquals(25.3, energySummary.todayEnergy)
        assertEquals(3.5, energySummary.todayIncome)
    }

    @Test
    fun getPlantEnergyHistory_returnsEnergyDataList() = runBlocking {
        val energyHistoryJson = """
            [
                {
                    "date": "2026-02-01",
                    "energy": 30.5,
                    "income": 4.2
                },
                {
                    "date": "2026-02-02",
                    "energy": 28.3,
                    "income": 3.9
                }
            ]
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/panel/getPlantEnergyData" -> respond(
                    content = energyHistoryJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
                else -> respondBadRequest()
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val api = PlantApiImpl(client, baseUrl = "https://server.growatt.com")
        val dateRange = DateRange(startDate = "2026-02-01", endDate = "2026-02-02")
        val energyHistory = api.getPlantEnergyHistory("123", dateRange)
        
        assertEquals(2, energyHistory.size)
        assertEquals("2026-02-01", energyHistory[0].date)
        assertEquals(30.5, energyHistory[0].energy)
        assertEquals(4.2, energyHistory[0].income)
    }

    @Test
    fun getPlantDevices_returnsDeviceList() = runBlocking {
        val devicesJson = """
            {
                "result": 1,
                "obj": {
                    "currPage": 1,
                    "pages": 1,
                    "pageSize": 10,
                    "count": 2,
                    "ind": 0,
                    "notPager": false,
                    "datas": [
                        {
                            "deviceType": "inverter",
                            "ptoStatus": "1",
                            "timeServer": "2026-02-08",
                            "accountName": "test",
                            "timezone": "0",
                            "plantId": "123",
                            "deviceTypeName": "Inverter",
                            "bdcNum": "1",
                            "nominalPower": "5000",
                            "bdcStatus": "1",
                            "eToday": "25.3",
                            "eMonth": "500.0",
                            "datalogTypeTest": "test",
                            "eTotal": "1500.0",
                            "pac": "3500",
                            "datalogSn": "SN123456",
                            "alias": "Inverter 1",
                            "location": "Roof",
                            "deviceModel": "Model-X",
                            "sn": "INV123",
                            "plantName": "Test Plant",
                            "status": "1",
                            "lastUpdateTime": "2026-02-08 12:00:00"
                        },
                        {
                            "deviceType": "inverter",
                            "ptoStatus": "1",
                            "timeServer": "2026-02-08",
                            "accountName": "test",
                            "timezone": "0",
                            "plantId": "123",
                            "deviceTypeName": "Inverter",
                            "bdcNum": "1",
                            "nominalPower": "5000",
                            "bdcStatus": "1",
                            "eToday": "20.1",
                            "eMonth": "400.0",
                            "datalogTypeTest": "test",
                            "eTotal": "1200.0",
                            "pac": "3000",
                            "datalogSn": "SN123457",
                            "alias": "Inverter 2",
                            "location": "Ground",
                            "deviceModel": "Model-Y",
                            "sn": "INV456",
                            "plantName": "Test Plant",
                            "status": "1",
                            "lastUpdateTime": "2026-02-08 12:00:00"
                        }
                    ]
                }
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/panel/getDevicesByPlantList" -> respond(
                    content = devicesJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
                else -> respondBadRequest()
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val api = PlantApiImpl(client, baseUrl = "https://server.growatt.com")
        val devices = api.getPlantDevices("123")
        
        assertEquals(2, devices.size)
        assertEquals("INV123", devices[0].id)
        assertEquals("Inverter 1", devices[0].name)
        assertEquals("1", devices[0].status)
        assertEquals("INV456", devices[1].id)
        assertEquals("Inverter 2", devices[1].name)
    }

    @Test
    fun getPlantDevices_withEmptyResponse_returnsEmptyList() = runBlocking {
        val devicesJson = """
            {
                "result": 1,
                "obj": null
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/panel/getDevicesByPlantList" -> respond(
                    content = devicesJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
                else -> respondBadRequest()
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val api = PlantApiImpl(client, baseUrl = "https://server.growatt.com")
        val devices = api.getPlantDevices("123")
        
        assertTrue(devices.isEmpty())
    }
}
