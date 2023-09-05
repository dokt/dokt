@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    signing
}

description = "Dokt domain logic API to be used in domain layer and its infrastructure implementations."

kotlin {
    configureJvm()

    sourceSets {
        commonMainDependencies {
            api(project(":dokt-common"))
        }
    }
}

publishing(createDoktPublication("Dokt domain API"))

signing(configureDoktSigning(publishing))
