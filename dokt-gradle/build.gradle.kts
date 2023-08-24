plugins {
    `java-gradle-plugin`
    //`kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish")
    kotlin("jvm")
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
    implementation(kotlin("serialization", "_"))

    implementation(project(":dokt-generator-app"))
}

java {
    withSourcesJar()
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
            implementationClass = "app.dokt.gradle.build.DoktSettingsPlugin"
        }
    }
}
