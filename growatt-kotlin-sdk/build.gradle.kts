import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.vanniktech.mavenPublish)
    // Add Kotlinx Serialization plugin for multiplatform
    alias(libs.plugins.kotlinx.serialization)
    signing
}

val local = Properties()
val localProperties: File = rootProject.file("local.properties")
if (localProperties.exists()) {
    localProperties.inputStream().use { local.load(it) }
}

group = "uk.co.andyreed"
version = "0.0.5"

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ktor.client.mock)
        }

        // Platform-specific engines
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)

    signAllPublications()

    coordinates(group.toString(), "growatt-kotlin-sdk", version.toString())

    pom {
        name = "Growatt Kotlin SDK"
        description = "A Kotlin SDK for interacting with Growatt solar inverters and energy storage systems."
        inceptionYear = "2026"
        url = "https://github.com/reedyuk/growatt-kotlin-sdk/"
        developers {
            developer {
                name.set("Andrew Reed")
                email.set("andrew_reed@hotmail.com")
            }
        }

        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
                comments.set("A business-friendly OSS license")
            }
        }
        scm {
            url.set("https://github.com/reedyuk/growatt-kotlin-sdk")
            connection.set("scm:git:https://github.com/reedyuk/growatt-kotlin-sdk.git")
            developerConnection.set("scm:git:https://github.com/reedyuk/growatt-kotlin-sdk.git")
            tag.set("HEAD")
        }

        issueManagement {
            system.set("GitHub Issues")
            url.set("https://github.com/reedyuk/growatt-kotlin-sdk/issues")
        }
    }
}

signing {
    setRequired {
        !gradle.taskGraph.allTasks.any { it is PublishToMavenLocal }
    }
    val key = local.getProperty("signingKey") ?: System.getenv("SIGNING_KEY")
    val password = local.getProperty("signingPassword") ?: System.getenv("SIGNING_PASSWORD")
    if (key != null && password != null) {
        useInMemoryPgpKeys(key, password)
        sign(publishing.publications) // This ensures all created publications are signed
    } else {
        // Optional: Log a warning if keys are missing for a release build
        logger.warn("Signing key or password not found. Publication will not be signed.")
    }
}
