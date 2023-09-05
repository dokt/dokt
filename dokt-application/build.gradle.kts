@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlinx.kover")
    `maven-publish`
    signing
}

description = "Dokt application library to be used in application layer and its infrastructure implementations."

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

publishing(createDoktPublication("Dokt application layer API"))

signing(configureDoktSigning(publishing))
