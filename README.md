# Growatt Kotlin SDK

A Kotlin Multiplatform SDK for interacting with Growatt solar inverters and energy storage systems.

Access real-time data from your Growatt solar installation including solar generation, battery status, grid power flows, and consumption metrics.

## Supported Platforms

- **JVM** (Android, Desktop, Server)
- **iOS** (iOS, iPadOS, macOS via Catalyst)

## Installation

Add the dependency to your project:

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("uk.co.andyreed:growatt-kotlin-sdk:0.0.4")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'uk.co.andyreed:growatt-kotlin-sdk:0.0.4'
}
```

## Quick Start

### 1. Authentication

First, authenticate with your Growatt account credentials:

```kotlin
import uk.co.andyreed.growatt.api.GrowattApiImpl
import uk.co.andyreed.growatt.http.createHttpClient

val client = createHttpClient()
val api = GrowattApiImpl(client)

// Login with your Growatt credentials
api.login("your-username", "your-password")

if (api.isAuthenticated) {
    println("Successfully authenticated!")
}
```

### 2. Get Your Plants

Retrieve a list of your solar installations:

```kotlin
val plants = api.getPlantList()

plants.forEach { plant ->
    println("Plant: ${plant.plantName} (ID: ${plant.id})")
}
```

### 3. Get Plant Details

Access detailed information about a specific plant:

```kotlin
import uk.co.andyreed.growatt.api.PlantApiImpl

val plantApi = PlantApiImpl(client)
val plantDetail = plantApi.getPlantDetail(plants.first().id)

println("Location: ${plantDetail.city}, ${plantDetail.country}")
println("Total Energy: ${plantDetail.eTotal} kWh")
println("CO2 Reduction: ${plantDetail.co2} kg")
```

### 4. Get Plant Devices

List all devices (inverters, batteries) for a plant:

```kotlin
val devices = plantApi.getPlantDevices(plantId = plants.first().id)

devices.forEach { device ->
    println("Device: ${device.name} (${device.status})")
}
```

## Real-time Data

### Get Complete System Snapshot

Fetch all real-time data (battery, solar, grid, consumption) in a single call:

```kotlin
import uk.co.andyreed.growatt.api.DeviceApiImpl

val deviceApi = DeviceApiImpl(client)

// Get today's date in yyyy-MM-dd format
val today = "2026-02-16"

val snapshot = deviceApi.getRealtimeSnapshot(
    date = today,
    plantId = "your-plant-id",
    storageSn = "your-storage-serial-number"
)

// Solar data
println("☀️ Solar Power: ${snapshot.solar.currentPower} W")
println("   Energy Today: ${snapshot.solar.energyToday} kWh")
println("   Total Energy: ${snapshot.solar.energyTotal} kWh")

// Battery data
println("🔋 Battery: ${snapshot.battery.stateOfCharge}%")
println("   Status: ${snapshot.battery.status}")
println("   Power: ${snapshot.battery.power} W")
println("   Charged Today: ${snapshot.battery.chargedToday} kWh")
println("   Discharged Today: ${snapshot.battery.dischargedToday} kWh")

// Grid data
println("⚡ Grid Export: ${snapshot.grid.powerToGrid} W")
println("   Grid Import: ${snapshot.grid.powerFromGrid} W")
println("   Net Power: ${snapshot.grid.netPower} W")

// Consumption data
println("🏠 Current Load: ${snapshot.consumption.currentLoad} W")
println("   From Solar: ${snapshot.consumption.powerFromSolar} W")
println("   From Battery: ${snapshot.consumption.powerFromBattery} W")
```

### Battery Charge/Discharge Charts

Get battery state of charge and charge/discharge data:

```kotlin
val batteryChart = deviceApi.getStorageBatChart(
    plantId = "your-plant-id",
    storageSn = "your-storage-serial-number"
)

// State of charge over time
batteryChart.socChart.capacity.forEach { soc ->
    println("Battery SoC: ${soc}%")
}

// Charge/discharge amounts
batteryChart.cdsData.cd_charge.forEachIndexed { index, charge ->
    println("Day ${index + 1}: Charged ${charge} kWh")
}
```

### Energy Day Charts

Get detailed power flow data throughout the day:

```kotlin
val energyChart = deviceApi.getStorageEnergyDayChart(
    date = "2026-02-16",
    plantId = "your-plant-id",
    storageSn = "your-storage-serial-number"
)

// Summary
println("Total Charged: ${energyChart.eChargeTotal} kWh")
println("Total Discharged: ${energyChart.eDisChargeTotal} kWh")

// Time-series data
energyChart.charts.ppv.forEach { solarPower ->
    println("Solar: $solarPower W")
}

energyChart.charts.userLoad.forEach { load ->
    println("Load: $load W")
}
```

## Weather Data

Get current weather conditions for a plant location:

```kotlin
val weather = api.getWeather(plantId = plants.first().id)
println("Weather data retrieved for plant location")
```

## API Reference

### GrowattApi
- `login(username: String, password: String)` - Authenticate with Growatt
- `getPlantList()` - Get list of your solar plants
- `getDevices(plantId: String, pageNumber: Int)` - Get devices for a plant
- `getWeather(plantId: String)` - Get weather data for plant location

### PlantApi
- `getPlantDetail(plantId: String)` - Get detailed plant information
- `getPlantDevices(plantId: String)` - Get list of devices for a plant

### DeviceApi
- `getRealtimeSnapshot(date: String, plantId: String, storageSn: String)` - Get unified real-time data snapshot
- `getStorageBatChart(plantId: String, storageSn: String)` - Get battery state of charge chart
- `getStorageEnergyDayChart(date: String, plantId: String, storageSn: String)` - Get energy flow chart

## Date Format

All date parameters use the format: `yyyy-MM-dd` (e.g., `"2026-02-16"`)

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.
