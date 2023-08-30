@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT", "Dokt common logging free language utilities.")

kotlin {
    configureJvmWithTests()

    sourceSets {
        commonMainDependencies {
            api(libs.uuid)
            api(libs.kotlinx.datetime)
            api(libs.kotlinx.serialization.core)
        }

        commonTestDependencies {
            implementation(project(":dokt-test"))
            implementation(libs.kotest.runner.junit5)
        }

        jvmMainDependencies {
            api(libs.commons.lang3)
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication("Dokt common utils"))

signing(configureDoktSigning(publishing))
