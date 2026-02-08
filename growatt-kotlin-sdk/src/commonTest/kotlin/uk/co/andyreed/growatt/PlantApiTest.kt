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
                "result": 1,
                "obj": {
                    "id": "10672078",
                    "plantName": "Derby",
                    "country": "UnitedKingdom",
                    "city": "Derby",
                    "timezone": "0",
                    "lat": "52.872",
                    "lng": "-1.441",
                    "accountName": "Reedyuk",
                    "nominalPower": "1000",
                    "eTotal": "2.5",
                    "moneyUnit": "GBP",
                    "moneyUnitText": "£",
                    "co2": "2.5",
                    "coal": "1",
                    "tree": "1",
                    "fixedPowerPrice": "1.2",
                    "valleyPeriodPrice": "1.0",
                    "peakPeriodPrice": "1.3",
                    "flatPeriodPrice": "1.1",
                    "creatDate": "2026-01-11",
                    "plantType": "0",
                    "isShare": "false",
                    "tempType": "0"
                }
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
        
        assertEquals("10672078", plantDetail.id)
        assertEquals("Derby", plantDetail.plantName)
        assertEquals("UnitedKingdom", plantDetail.country)
        assertEquals("Derby", plantDetail.city)
        assertEquals("2.5", plantDetail.eTotal)
    }

    @Test
    fun getPlantEnergyToday_returnsEnergySummary() = runBlocking {
        val plantDetailJson = """
            {
                "result": 1,
                "obj": {
                    "id": "10672078",
                    "plantName": "Derby",
                    "eTotal": "2.5"
                }
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
        
        // Note: The getPlantData endpoint doesn't provide todayEnergy/todayIncome
        assertEquals(0.0, energySummary.todayEnergy)
        assertEquals(0.0, energySummary.todayIncome)
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
