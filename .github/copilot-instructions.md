# Growatt Kotlin SDK - Development Guide

## Overview

This is a Kotlin Multiplatform SDK for interacting with the Growatt solar inverter API (https://server.growatt.com/). The SDK supports JVM, iOS (x64, ARM64, and Simulator ARM64) platforms.

## Build, Test, and Lint

### Building
```bash
./gradlew build
```

### Testing
Run all tests:
```bash
./gradlew test
```

Run tests for a specific platform:
```bash
./gradlew jvmTest          # JVM tests only
./gradlew iosSimulatorArm64Test  # iOS simulator tests
```

Run a single test class:
```bash
./gradlew test --tests "uk.co.andyreed.growatt.GrowattClientTest"
```

### Publishing Locally
```bash
./gradlew publishToMavenLocal
```

## Architecture

### Multiplatform Structure

The project follows Kotlin Multiplatform conventions with platform-specific source sets:

- **commonMain**: Shared code for all platforms
  - API interfaces and implementations (GrowattApi, PlantApi, DeviceApi)
  - Data models with kotlinx.serialization annotations
  - Expect declarations for platform-specific functionality
  
- **commonTest**: Shared test code using Ktor MockEngine

- **jvmMain**: JVM-specific implementations
  - HttpClientFactory using OkHttp engine
  
- **iosMain**: iOS-specific implementations
  - HttpClientFactory using Darwin engine

### API Organization

The SDK is organized into three main API interfaces:

1. **GrowattApi** (GrowattApiImpl)
   - Authentication (`login()`)
   - Plant listing (`getPlantList()`)
   - Basic device queries (`getDevices()`)
   - Weather data (`getWeather()`)

2. **PlantApi** (PlantApiImpl)
   - Plant details (`getPlantDetail()`)
   - Plant devices (`getPlantDevices()`)

3. **DeviceApi** (DeviceApiImpl)
   - Storage battery charts (`getStorageBatChart()`)
   - Energy day charts (`getStorageEnergyDayChart()`)

All API implementations:
- Use Ktor HttpClient for HTTP communication
- Default to `https://server.growatt.com` but accept custom baseUrl
- Use kotlinx.serialization for JSON parsing with `ignoreUnknownKeys = true`
- Print raw response bodies to stdout for debugging

### HttpClient Pattern

Each platform provides its own HttpClient through the expect/actual pattern:
- `expect fun createHttpClient(debug: Boolean = false): HttpClient` in commonMain
- Actual implementations in jvmMain (OkHttp) and iosMain (Darwin)

## Key Conventions

### Authentication State

The `GrowattApi` interface exposes `isAuthenticated: Boolean` as a mutable property. Implementations set this flag based on login response. The SDK relies on cookies maintained by the underlying HTTP engine for subsequent authenticated requests.

### Response Wrapping

The Growatt API returns responses in a wrapper format:
```kotlin
{ "result": <code>, "obj": <actual_data> }
```

When the `obj` field is null or missing, implementations throw `ApiException(result, message)`.

### Date Formatting

Device API methods that accept dates expect `yyyy-MM-dd` format (e.g., `"2026-02-15"`).

### Real-time Data Snapshot

The `DeviceApi.getRealtimeSnapshot()` method provides a unified view of all system real-time data:
- **Solar**: Current power generation, energy today/total
- **Battery**: State of charge, charging/discharging power and status
- **Grid**: Import/export power flows
- **Consumption**: Current load and power sources

This method internally calls both `getStorageBatChart()` and `getStorageEnergyDayChart()`, then extracts the latest values from time-series data to provide a current snapshot.

### Testing with Mock Engine

Tests use Ktor's MockEngine to simulate API responses. When writing tests:
- Match on `request.url.encodedPath`
- Use `respond()` with JSON string content
- Set appropriate content-type headers
- Install ContentNegotiation plugin on the mock client

## Publishing

The SDK publishes to Maven Central using the Vanniktech Maven Publish plugin. Publishing is automated via GitHub Actions:

- **Pull Requests**: Build and test on every PR
- **Releases**: Automatically publish to Maven Central when a GitHub release is created

### Local Signing

For local publishing with signing, create `local.properties` with:
```properties
signingKey=<your-gpg-key>
signingPassword=<your-passphrase>
```

Signing is optional for `publishToMavenLocal` but required for Maven Central releases.

## Dependencies

- **Ktor**: HTTP client (core, content negotiation, logging, platform engines)
- **kotlinx.serialization**: JSON serialization
- **Kotlin Test**: Testing framework
