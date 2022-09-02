@file:Suppress("UNUSED_VARIABLE")

plugins {
    `maven-publish`
    signing
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

description = "Dokt Application API for defining application layer and its infrastructure."

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
                api("io.github.microutils:kotlin-logging:_")
                api(project(":dokt-domain"))
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
