# growatt-kotlin-sdk
Growatt Api Kotlin SDK

An attempt to replicate the data and functionality of https://server.growatt.com/ using Kotlin.

## Usage

### Getting Real-time Data

Fetch all real-time data (battery, solar, grid, consumption) in a single call:

```kotlin
import uk.co.andyreed.growatt.api.DeviceApiImpl
import uk.co.andyreed.growatt.http.createHttpClient

val client = createHttpClient(debug = false)
val deviceApi = DeviceApiImpl(client)

// Fetch real-time snapshot (use today's date in yyyy-MM-dd format)
val snapshot = deviceApi.getRealtimeSnapshot(
    date = "2026-02-15",
    plantId = "your-plant-id",
    storageSn = "your-storage-serial-number"
)

// Access solar data
println("Solar Power: ${snapshot.solar.currentPower} W")
println("Energy Today: ${snapshot.solar.energyToday} kWh")

// Access battery data
println("Battery SoC: ${snapshot.battery.stateOfCharge}%")
println("Battery Status: ${snapshot.battery.status}")
println("Battery Power: ${snapshot.battery.power} W")

// Access grid data
println("Grid Export: ${snapshot.grid.powerToGrid} W")
println("Grid Import: ${snapshot.grid.powerFromGrid} W")

// Access consumption data
println("Load: ${snapshot.consumption.currentLoad} W")
println("Power from Solar: ${snapshot.consumption.powerFromSolar} W")
println("Power from Battery: ${snapshot.consumption.powerFromBattery} W")
```

### Authentication

```kotlin
import uk.co.andyreed.growatt.api.GrowattApiImpl
import uk.co.andyreed.growatt.http.createHttpClient

val client = createHttpClient()
val api = GrowattApiImpl(client)

// Login
val authResponse = api.login("username", "password")
if (api.isAuthenticated) {
    // Fetch plant list
    val plants = api.getPlantList()
    
    // Get devices for a plant
    val devices = api.getDevices(plants.first().id, pageNumber = 1)
}
```

## CI/CD

This project uses GitHub Actions for continuous integration and publishing to Maven Central.

### Workflows

- **Pull Requests**: Runs build and tests on every pull request
- **Release**: Publishes to Maven Central when a GitHub release is created

### Required Secrets for Publishing

To publish to Maven Central, configure these secrets in your GitHub repository settings:

- `SONATYPEUSERNAME`: Your Sonatype OSSRH username
- `SONATYPEPASSWORD`: Your Sonatype OSSRH password or token
- `GPG_KEY`: Your GPG private key in ASCII-armored format (export with `gpg --armor --export-secret-keys YOUR_KEY_ID`)
- `GPG_KEY_PASS`: Your GPG key passphrase

### Publishing a Release

1. Create a new release on GitHub with a tag (e.g., `v0.0.1`)
2. The release workflow will automatically build and publish to Maven Central
