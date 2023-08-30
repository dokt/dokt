@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT", "Dokt interface API to be used in presentation layer.")

kotlin {
    configureJvmWithTests()

    sourceSets {
        commonMainDependencies {
            api(project(":dokt-application"))
        }

        jvmTestDependencies {
            implementation(libs.kotest.runner.junit5)
            runtimeOnly(libs.logback.classic)
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication("Dokt interface API"))

signing(configureDoktSigning(publishing))
