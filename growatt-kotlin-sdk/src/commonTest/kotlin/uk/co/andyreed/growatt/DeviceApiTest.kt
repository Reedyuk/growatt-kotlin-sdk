package uk.co.andyreed.growatt

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.engine.mock.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.http.*
import uk.co.andyreed.growatt.api.*

class DeviceApiTest {

    @Test
    fun getStorageBatChart_returnsStorageBatChartData() = runBlocking {
        val storageBatChartJson = """
            {
                "result": 1,
                "obj": {
                    "date": "2026-02-11",
                    "cdsTitle": ["2026-02-05", "2026-02-06", "2026-02-07"],
                    "socChart": {
                        "capacity": [null, 38.0, 39.0, 40.0, null]
                    },
                    "cdsData": {
                        "cd_charge": [0.0, 0.1, 0.2],
                        "cd_disCharge": [0.0, 0.0, 0.1]
                    }
                }
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("/panel/storage/getStorageBatChart") -> {
                    respond(
                        content = storageBatChartJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> respondBadRequest()
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val api = DeviceApiImpl(client, baseUrl = "https://server.growatt.com")
        val chartData = api.getStorageBatChart("123", "abc")
        
        assertEquals("2026-02-11", chartData.date)
        assertEquals(3, chartData.cdsTitle.size)
        assertEquals("2026-02-05", chartData.cdsTitle[0])
        assertEquals(5, chartData.socChart.capacity.size)
        assertEquals(null, chartData.socChart.capacity[0])
        assertEquals(38.0, chartData.socChart.capacity[1])
        assertEquals(3, chartData.cdsData.cd_charge.size)
        assertEquals(0.0, chartData.cdsData.cd_charge[0])
        assertEquals(0.1, chartData.cdsData.cd_charge[1])
        assertEquals(3, chartData.cdsData.cd_disCharge.size)
        assertEquals(0.0, chartData.cdsData.cd_disCharge[0])
    }

    @Test
    fun getStorageEnergyDayChart_returnsStorageEnergyDayChartData() = runBlocking {
        val storageEnergyDayChartJson = """
            {
                "result": 1,
                "obj": {
                    "eChargeTotal": "0.5",
                    "charts": {
                        "pacToGrid": [null, 0.0, 10.5, null],
                        "ppv": [null, 0.0, 67.5, 88.0],
                        "sysOut": [null, 0.0, 0.0, null],
                        "userLoad": [null, 0.0, 0.0, null],
                        "pacToUser": [null, 0.0, 0.0, null]
                    },
                    "dtc": 20006,
                    "eAcDisCharge": "0",
                    "eDisCharge": "0.2",
                    "eCharge": "0.5",
                    "eAcCharge": "0",
                    "eDisChargeTotal": "0.2"
                }
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("/panel/storage/getStorageEnergyDayChart") -> {
                    respond(
                        content = storageEnergyDayChartJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> respondBadRequest()
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val api = DeviceApiImpl(client, baseUrl = "https://server.growatt.com")
        val energyData = api.getStorageEnergyDayChart("2026-02-11", "123", "abc")
        
        assertEquals("0.5", energyData.eChargeTotal)
        assertEquals(20006, energyData.dtc)
        assertEquals("0", energyData.eAcDisCharge)
        assertEquals("0.2", energyData.eDisCharge)
        assertEquals("0.5", energyData.eCharge)
        assertEquals("0", energyData.eAcCharge)
        assertEquals("0.2", energyData.eDisChargeTotal)
        
        assertEquals(4, energyData.charts.pacToGrid.size)
        assertEquals(null, energyData.charts.pacToGrid[0])
        assertEquals(0.0, energyData.charts.pacToGrid[1])
        assertEquals(10.5, energyData.charts.pacToGrid[2])
        
        assertEquals(4, energyData.charts.ppv.size)
        assertEquals(67.5, energyData.charts.ppv[2])
        assertEquals(88.0, energyData.charts.ppv[3])
    }
}
