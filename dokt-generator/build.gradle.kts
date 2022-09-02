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
                implementation("io.github.microutils:kotlin-logging:_")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(Testing.kotest.runner.junit5)
                runtimeOnly("ch.qos.logback:logback-classic:_")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(Square.kotlinPoet)
                // TODO `1.5.31` that might work differently than in the requested version `1.6.21`
                implementation(kotlin("compiler-embeddable"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(Testing.kotest.runner.junit5)
                runtimeOnly("ch.qos.logback:logback-classic:_")
            }
        }
    }
}
