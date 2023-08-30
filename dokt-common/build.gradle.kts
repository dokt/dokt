@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.9.10"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT", "Dokt common logging free language utilities.")

kotlin {
    configureJvmWithTests()

    sourceSets {
        commonMainDependencies {
            api("com.benasher44:uuid:0.8.1")
            api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")
        }

        commonTestDependencies {
            implementation(project(":dokt-test"))
            implementation("io.kotest:kotest-runner-junit5:5.6.2")
        }

        jvmMainDependencies {
            api("org.apache.commons:commons-lang3:3.13.0")
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication("Dokt common utils"))

signing(configureDoktSigning(publishing))
