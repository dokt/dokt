@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.9.10"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
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
            implementation("io.kotest:kotest-runner-junit5:5.6.2")
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication("Dokt domain test API"))

signing(configureDoktSigning(publishing))
