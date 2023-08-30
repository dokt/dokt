@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT",
    "Dokt application library to be used in application layer and its infrastructure implementations.")

kotlin {
    configureJvmWithTests()

    sourceSets {
        commonMainDependencies {
            api(project(":dokt-domain"))
            api(project(":dokt-infrastructure"))
            // TODO On Events.kt: "Cannot access class 'kotlin.coroutines.CoroutineContext'.
            // TODO Check your module classpath for missing or conflicting dependencies"
            // TODO api(KotlinX.coroutines.core)
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

publishing(createDoktPublication("Dokt application layer API"))

signing(configureDoktSigning(publishing))
