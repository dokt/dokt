@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
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
            implementation("io.kotest:kotest-runner-junit5:5.6.2")
            runtimeOnly("ch.qos.logback:logback-classic:1.4.11")
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication("Dokt interface API"))

signing(configureDoktSigning(publishing))
