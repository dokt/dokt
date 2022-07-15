plugins {
    `maven-publish`
    signing
    kotlin("multiplatform")
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
        @Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
                api(Testing.kotest.assertions.core)
                api(Testing.kotest.framework.api)
            }
        }
    }
}
