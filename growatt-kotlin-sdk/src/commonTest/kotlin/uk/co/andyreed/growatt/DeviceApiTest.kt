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
    fun getDetail_returnsDeviceDetail() = runBlocking {
        val deviceDetailJson = """
            {
                "deviceId": "device1",
                "deviceSn": "SN123456",
                "deviceType": "inverter",
                "deviceModel": "MIC 3000TL-X",
                "plantId": "plant1",
                "plantName": "My Plant",
                "nominalPower": "3000",
                "location": "Home",
                "timezone": "0",
                "datalogSn": "DL123456",
                "alias": "Main Inverter"
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("/panel/getDeviceDetail") -> {
                    respond(
                        content = deviceDetailJson,
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
        val detail = api.getDetail("device1")
        
        assertEquals("device1", detail.deviceId)
        assertEquals("SN123456", detail.deviceSn)
        assertEquals("inverter", detail.deviceType)
    }

    @Test
    fun getRealtime_returnsRealtimeData() = runBlocking {
        val realtimeJson = """
            {
                "deviceId": "device1",
                "pac": "2500",
                "eToday": "15.5",
                "eTotal": "1234.5",
                "lastUpdateTime": "2026-02-08 12:00:00",
                "status": "1"
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("/panel/getDeviceRealtime") -> {
                    respond(
                        content = realtimeJson,
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
        val realtime = api.getRealtime("device1")
        
        assertEquals("device1", realtime.deviceId)
        assertEquals("2500", realtime.pac)
        assertEquals("15.5", realtime.eToday)
    }

    @Test
    fun getHistory_returnsHistoryEntries() = runBlocking {
        val historyJson = """
            [
                {
                    "date": "2026-02-01",
                    "energy": "12.5",
                    "power": "2000"
                },
                {
                    "date": "2026-02-02",
                    "energy": "13.2",
                    "power": "2100"
                }
            ]
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("/panel/getDeviceHistory") -> {
                    respond(
                        content = historyJson,
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
        val range = DateRange("2026-02-01", "2026-02-08")
        val history = api.getHistory("device1", range)
        
        assertEquals(2, history.size)
        assertEquals("2026-02-01", history[0].date)
        assertEquals("12.5", history[0].energy)
    }

    @Test
    fun getStatus_returnsDeviceStatus() = runBlocking {
        val statusJson = """
            {
                "deviceId": "device1",
                "status": "1",
                "ptoStatus": "online",
                "bdcStatus": "ok",
                "lastUpdateTime": "2026-02-08 12:00:00"
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("/panel/getDeviceStatus") -> {
                    respond(
                        content = statusJson,
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
        val status = api.getStatus("device1")
        
        assertEquals("device1", status.deviceId)
        assertEquals("1", status.status)
        assertEquals("online", status.ptoStatus)
    }

    @Test
    fun getAlarms_returnsAlarms() = runBlocking {
        val alarmsJson = """
            [
                {
                    "alarmId": "alarm1",
                    "deviceId": "device1",
                    "alarmCode": "E001",
                    "alarmMessage": "Grid voltage too high",
                    "alarmType": "warning",
                    "timestamp": "2026-02-08 10:00:00",
                    "isResolved": false
                },
                {
                    "alarmId": "alarm2",
                    "deviceId": "device1",
                    "alarmCode": "E002",
                    "alarmMessage": "Temperature high",
                    "alarmType": "info",
                    "timestamp": "2026-02-08 09:00:00",
                    "isResolved": true
                }
            ]
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("/panel/getDeviceAlarms") -> {
                    respond(
                        content = alarmsJson,
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
        val alarms = api.getAlarms("device1")
        
        assertEquals(2, alarms.size)
        assertEquals("alarm1", alarms[0].alarmId)
        assertEquals("E001", alarms[0].alarmCode)
        assertEquals(false, alarms[0].isResolved)
        assertEquals(true, alarms[1].isResolved)
    }

    @Test
    fun getBatteryState_returnsBatteryState() = runBlocking {
        val batteryStateJson = """
            {
                "deviceId": "device1",
                "soc": "75.5",
                "voltage": "52.4",
                "current": "10.2",
                "power": "534",
                "temperature": "25.3",
                "status": "charging",
                "lastUpdateTime": "2026-02-08 12:00:00"
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("/panel/getBatteryState") -> {
                    respond(
                        content = batteryStateJson,
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
        val batteryState = api.getBatteryState("device1")
        
        assertEquals("device1", batteryState.deviceId)
        assertEquals("75.5", batteryState.soc)
        assertEquals("52.4", batteryState.voltage)
        assertEquals("10.2", batteryState.current)
        assertEquals("534", batteryState.power)
        assertEquals("25.3", batteryState.temperature)
        assertEquals("charging", batteryState.status)
    }

    @Test
    fun getBatteryMetrics_returnsBatteryMetrics() = runBlocking {
        val batteryMetricsJson = """
            {
                "deviceId": "device1",
                "capacity": "10.0",
                "remainingCapacity": "7.5",
                "chargedToday": "5.2",
                "dischargedToday": "3.8",
                "chargedTotal": "2500.5",
                "dischargedTotal": "2300.2",
                "cycleCount": "250",
                "health": "95.5",
                "lastUpdateTime": "2026-02-08 12:00:00"
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("/panel/getBatteryMetrics") -> {
                    respond(
                        content = batteryMetricsJson,
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
        val batteryMetrics = api.getBatteryMetrics("device1")
        
        assertEquals("device1", batteryMetrics.deviceId)
        assertEquals("10.0", batteryMetrics.capacity)
        assertEquals("7.5", batteryMetrics.remainingCapacity)
        assertEquals("5.2", batteryMetrics.chargedToday)
        assertEquals("3.8", batteryMetrics.dischargedToday)
        assertEquals("2500.5", batteryMetrics.chargedTotal)
        assertEquals("2300.2", batteryMetrics.dischargedTotal)
        assertEquals("250", batteryMetrics.cycleCount)
        assertEquals("95.5", batteryMetrics.health)
    }
}
