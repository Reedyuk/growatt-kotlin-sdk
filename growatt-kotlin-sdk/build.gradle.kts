import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    // Add Kotlinx Serialization plugin for multiplatform
    alias(libs.plugins.kotlinx.serialization)
    signing
}

group = "uk.co.andyreed"
version = "0.0.1"

kotlin {
    jvm()
    androidLibrary {
        namespace = "uk.co.andyreed"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava() // enable java compilation support
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(
                    JvmTarget.JVM_11
                )
            }
        }
    }
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
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

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
}
