@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt")
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
            implementation(libs.kotest.runner.junit5)
            runtimeOnly(libs.logback.classic)
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication("Dokt application layer API"))

signing(configureDoktSigning(publishing))
