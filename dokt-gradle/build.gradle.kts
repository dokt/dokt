plugins {
    `java-gradle-plugin`
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
            displayName = "Dokt plugin for the root project"
            description = project.description
            implementationClass = "app.dokt.gradle.RootProjectPlugin"
        }
        create("dokt-domain") {
            id = "app.dokt.domain"
            displayName = "Dokt plugin for domain projects"
            description = project.description
            implementationClass = "app.dokt.gradle.DomainProjectPlugin"
        }
    }
}
