@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    signing
}

group = "app.dokt"
version = "0.2.11-SNAPSHOT"
description = "A Dokt application library to be used in application layer and its infrastructure implementations."

repositories {
    mavenCentral()
}

kotlin {
    jvmUsingJUnitPlatform()

    sourceSets {
        commonMainDependencies {
            api(project(":dokt-domain"))
            api(project(":dokt-infrastructure"))
        }

        commonTestDependencies {
            implementation(project(":dokt-test"))
            implementation(Testing.kotest.runner.junit5)
            runtimeOnly("ch.qos.logback:logback-classic:_")
        }
    }
}

publishing(createDoktPublication("Dokt application layer API"))

signing(configureDoktSigning(publishing))
