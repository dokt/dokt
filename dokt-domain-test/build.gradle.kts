@file:Suppress("SpellCheckingInspection")

plugins {
    `maven-publish`
    signing
    kotlin("multiplatform")
    kotlin("plugin.serialization")
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
                api(project(":dokt-domain"))
                api(project(":dokt-test"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Testing.kotest.runner.junit5)
            }
        }
    }
}
