@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.9.10"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT", "Common utilities for infrastructure services.")

kotlin {
    configureJvmWithTests()

    sourceSets {
        commonMainDependencies {
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            api(project(":dokt-common"))
            api("io.github.oshai:kotlin-logging:5.1.0")
        }

        commonTestDependencies {
            implementation(project(":dokt-test"))
            implementation("io.kotest:kotest-runner-junit5:5.6.2")
            runtimeOnly("ch.qos.logback:logback-classic:1.4.11")
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication("Dokt infrastructure"))

signing(configureDoktSigning(publishing))
