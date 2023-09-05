@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlinx.kover")
    `maven-publish`
    signing
}

description = "Common utilities for infrastructure services."

kotlin {
    configureJvmWithTests()

    sourceSets {
        commonMainDependencies {
            api(libs.kotlinx.coroutines.core)
            api(project(":dokt-common"))
            api(libs.kotlin.logging)
        }

        commonTestDependencies {
            implementation(project(":dokt-test"))
            implementation(libs.kotest.runner.junit5)
            runtimeOnly(libs.logback.classic)
        }
    }
}

publishing(createDoktPublication("Dokt infrastructure"))

signing(configureDoktSigning(publishing))
