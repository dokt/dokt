@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT", "Dokt code and documentation generator application.")

kotlin {
    configureJvmWithTests()

    sourceSets {
        commonMainDependencies {
            api(project(":dokt-application"))
            implementation(project(":dokt-domain-test"))
            implementation("io.github.oshai:kotlin-logging:5.1.0")
        }

        commonTestDependencies {
            api("io.kotest:kotest-runner-junit5:5.6.2")
            implementation("ch.qos.logback:logback-classic:1.4.11")
        }

        jvmMainDependencies {
            implementation("com.squareup:kotlinpoet:1.14.2")
            // Using `compiler-embeddable` because the base `compiler` is missing
            // `org.jetbrains.kotlin.com.intellij` packages.
            // See: https://discuss.kotlinlang.org/t/kotlin-compiler-embeddable-vs-kotlin-compiler/3196
            // There is also `kotlin-scripting-compiler-embeddable` in dependencies
            implementation(kotlin("compiler-embeddable"))
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication("Dokt generator"))

signing(configureDoktSigning(publishing))
