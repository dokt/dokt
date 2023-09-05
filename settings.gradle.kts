@file:Suppress("SpellCheckingInspection", "UnstableApiUsage")

plugins {
    id("de.fayard.refreshVersions") version "0.60.2"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "dokt"

include(
    "dokt-test",
    "dokt-common",
    "dokt-infrastructure",
    "dokt-domain",
    "dokt-domain-test",
    "dokt-application",
    "dokt-interface",
    "dokt-generator-app",
    "dokt-gradle",
    "dokt-ktor-server",
    "dokt-swing"
)
