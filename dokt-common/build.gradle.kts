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
                api("com.benasher44:uuid:_")
                api(KotlinX.datetime)
                api(KotlinX.serialization.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":dokt-test"))
                implementation(Testing.kotest.runner.junit5)
            }
        }
        val jvmMain by getting {
            dependencies {
                api("org.apache.commons:commons-lang3:_")
            }
        }
    }
}
