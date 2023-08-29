@file:Suppress("SpellCheckingInspection")

plugins {
    `java-gradle-plugin`
    //`kotlin-dsl`
    `maven-publish`
    kotlin("jvm")
    id("com.gradle.plugin-publish")
    id("io.gitlab.arturbosch.detekt")
}

description = "Domain-driven design using Kotlin"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.multiplatform
    implementation(kotlin("gradle-plugin", "_"))

    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.plugin.serialization
    implementation(kotlin("serialization", "_")) // TODO Is really needed

    implementation(project(":dokt-generator-app"))
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

gradlePlugin {
    website = "https://dokt.app/"
    vcsUrl = "https://github.com/dokt/dokt"
    //tags = listOf("code-generation", "codegen", "cqrs", "ddd", "es", "kotlin", "kotlin-mpp", "mpp")
    plugins {
        create("dokt") {
            id = "app.dokt"
            displayName = "Dokt plugin"
            description = project.description
            implementationClass = "app.dokt.gradle.building.DoktSettingsPlugin"
        }
    }
}
