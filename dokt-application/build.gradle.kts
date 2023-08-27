@file:Suppress("SpellCheckingInspection")

plugins {
    `maven-publish`
    signing
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
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
                api(project(":dokt-domain"))
                api(project(":dokt-infrastructure"))
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
