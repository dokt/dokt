plugins {
    `java-gradle-plugin`
    //`kotlin-dsl`
    `maven-publish`
    kotlin("jvm")
    id("com.gradle.plugin-publish")
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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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
