@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.9.10"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT",
    "Dokt domain logic API to be used in domain layer and its infrastructure implementations.")

kotlin {
    configureJvm()

    sourceSets {
        commonMainDependencies {
            api(project(":dokt-common"))
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication("Dokt domain API"))

signing(configureDoktSigning(publishing))
