@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt")
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
            implementation("io.github.oshai:kotlin-logging:_")
        }

        commonTestDependencies {
            api(Testing.kotest.runner.junit5)
            implementation("ch.qos.logback:logback-classic:_")
        }

        jvmMainDependencies {
            implementation(Square.kotlinPoet)
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
