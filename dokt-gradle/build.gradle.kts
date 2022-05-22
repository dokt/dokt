plugins {
    `java-gradle-plugin`
    `maven-publish`
    `kotlin-dsl`
    id("com.gradle.plugin-publish")
}

description = "Domain-driven design using Kotlin"

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("de.fayard.refreshVersions:refreshVersions:_")

    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.multiplatform
    implementation(kotlin("gradle-plugin", "_"))

    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.plugin.serialization
    implementation(kotlin("serialization", "_"))

    implementation(project(":dokt-generator"))
}

java {
    withSourcesJar()
}

pluginBundle {
    website = "https://dokt.app/"
    vcsUrl = "https://github.com/dokt/dokt"
    tags = listOf("code-generation", "codegen", "cqrs", "ddd", "es", "kotlin", "kotlin-mpp", "mpp")
}

gradlePlugin {
    plugins {
        create("dokt") {
            id = "app.dokt"
            displayName = "Dokt Plugin"
            description = project.description
            implementationClass = "app.dokt.gradle.DoktPlugin"
        }
    }
}
