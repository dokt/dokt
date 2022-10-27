@file:Suppress("UNUSED_VARIABLE")

plugins {
    `maven-publish`
    signing
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

description = "Common utilities to implement infrastucture services"

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
                api(KotlinX.coroutines.core)
                api(project(":dokt-common"))
                api("io.github.microutils:kotlin-logging:_")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":dokt-test"))
                implementation(Testing.kotest.runner.junit5)
                runtimeOnly("ch.qos.logback:logback-classic:_")
            }
        }
    }
}
