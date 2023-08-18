@file:Suppress("UNUSED_VARIABLE")

plugins {
    `maven-publish`
    signing
    kotlin("multiplatform")
}

description = "Dokt code and documentation generator."

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
                api(project(":dokt-application"))
                implementation(project(":dokt-domain-test"))
                implementation("io.github.oshai:kotlin-logging:_")
            }
        }

        val commonTest by getting {
            dependencies {
                api(Testing.kotest.runner.junit5)
                implementation("ch.qos.logback:logback-classic:_")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(Square.kotlinPoet)
                implementation(kotlin("compiler-embeddable"))
            }
        }
    }
}
