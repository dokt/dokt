@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlinx.kover")
    `maven-publish`
    signing
}

description = "Dokt code and documentation generator application."

kotlin {
    configureJvmWithTests()

    sourceSets {
        commonMainDependencies {
            api(project(":dokt-application"))
            implementation(project(":dokt-domain-test"))
            implementation(libs.kotlin.logging)
        }

        commonTestDependencies {
            api(libs.kotest.runner.junit5)
            implementation(libs.logback.classic)
        }

        jvmMainDependencies {
            implementation(libs.kotlinpoet)
            // Using `compiler-embeddable` because the base `compiler` is missing
            // `org.jetbrains.kotlin.com.intellij` packages.
            // See: https://discuss.kotlinlang.org/t/kotlin-compiler-embeddable-vs-kotlin-compiler/3196
            // There is also `kotlin-scripting-compiler-embeddable` in dependencies
            implementation(kotlin("compiler-embeddable"))
        }
    }
}

publishing(createDoktPublication("Dokt generator"))

signing(configureDoktSigning(publishing))
