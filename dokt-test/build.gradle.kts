@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    signing
}

description = "Common logging free test utilities."

kotlin {
    configureJvm()

    sourceSets {
        commonMainDependencies {
            api(libs.kotlinx.serialization.json)
            api(libs.kotest.assertions.core)
            api(libs.kotest.framework.api)
            api(libs.mockk)
        }
    }
}

publishing(createDoktPublication("Common test utils"))

signing(configureDoktSigning(publishing))
