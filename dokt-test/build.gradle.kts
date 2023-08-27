@file:Suppress("SpellCheckingInspection")

plugins {
    `maven-publish`
    signing
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt")
}

description = "Common (logging free) test utilities"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(KotlinX.serialization.json)
                api(Testing.kotest.assertions.core)
                api(Testing.kotest.framework.api)
                api(Testing.mockK)
            }
        }
    }
}
