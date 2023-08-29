@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT", "Common utilities for infrastructure services.")

kotlin {
    configureJvmWithTests()

    sourceSets {
        commonMainDependencies {
            api(KotlinX.coroutines.core)
            api(project(":dokt-common"))
            api("io.github.oshai:kotlin-logging:_")
        }

        commonTestDependencies {
            implementation(project(":dokt-test"))
            implementation(Testing.kotest.runner.junit5)
            runtimeOnly("ch.qos.logback:logback-classic:_")
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication("Dokt infrastructure"))

signing(configureDoktSigning(publishing))
