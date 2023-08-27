@file:Suppress("SpellCheckingInspection")

plugins {
    `maven-publish`
    signing
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
}

description = "Dokt Domain API for defining domain layer and its infrastructure."

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":dokt-common"))
            }
        }
    }
}
