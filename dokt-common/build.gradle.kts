@file:Suppress("UNUSED_VARIABLE")

plugins {
    `maven-publish`
    signing
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

description = "Common (logging free) language utilities"

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
                api(KotlinX.serialization.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":dokt-test"))
                implementation(KotlinX.serialization.json)
                implementation(Testing.kotest.runner.junit5)
            }
        }
    }
}
