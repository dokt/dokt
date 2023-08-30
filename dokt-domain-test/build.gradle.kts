@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlinx.kover")
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT",
    "Dokt domain logic test API to be used in unit testing of aggregate roots.")

kotlin {
    configureJvmWithTests()

    sourceSets {
        commonMainDependencies {
            api(project(":dokt-domain"))
            api(project(":dokt-test"))
        }

        commonTestDependencies {
            implementation(libs.kotest.runner.junit5)
        }
    }
}

publishing(createDoktPublication("Dokt domain test API"))

signing(configureDoktSigning(publishing))
