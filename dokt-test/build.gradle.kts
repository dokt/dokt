@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT", "Common logging free test utilities.")

kotlin {
    configureJvm()

    sourceSets {
        commonMainDependencies {
            api(KotlinX.serialization.json)
            api(Testing.kotest.assertions.core)
            api(Testing.kotest.framework.api)
            api(Testing.mockK)
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication("Common test utils"))

signing(configureDoktSigning(publishing))
