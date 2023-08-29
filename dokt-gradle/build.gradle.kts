@file:Suppress("SpellCheckingInspection")

plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    //`kotlin-dsl` TODO Remove if not used
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    id("com.gradle.plugin-publish")
}

setDoktDefaults("0.2.11-SNAPSHOT", "Domain-driven design using Kotlin")

repositories {
    gradlePluginPortal()
}

dependencies {
    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.multiplatform
    implementation(kotlin("gradle-plugin"))

    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.plugin.serialization
    //implementation(kotlin("serialization")) // TODO Is really needed

    implementation(project(":dokt-generator-app"))
}

java {
    withSourcesJar()
    sourceCompatibility = javaCompatibility
    targetCompatibility = javaCompatibility
}

kotlin {
    configureKotlin()
}

detekt {
    configureDetekt(config)
}

val pluginName = "Dokt plugin"
publishing(createDoktPublication(pluginName))

gradlePlugin {
    website = "https://dokt.app/"
    vcsUrl = "https://github.com/dokt/dokt"
    //tags = listOf("code-generation", "codegen", "cqrs", "ddd", "es", "kotlin", "kotlin-mpp", "mpp")
    plugins {
        create("dokt") {
            id = "app.dokt"
            displayName = pluginName
            description = project.description
            implementationClass = "app.dokt.gradle.building.DoktSettingsPlugin"
        }
    }
}
