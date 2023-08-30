@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT", "Common logging free test utilities.")

kotlin {
    configureJvm()

    sourceSets {
        commonMainDependencies {
            api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            api("io.kotest:kotest-assertions-core:5.6.2")
            api("io.kotest:kotest-framework-api:5.6.2")
            api("io.mockk:mockk:1.13.7")
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication("Common test utils"))

signing(configureDoktSigning(publishing))
