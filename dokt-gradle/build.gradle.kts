@file:Suppress("SpellCheckingInspection")

plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    id("com.gradle.plugin-publish")
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(project(":dokt-generator-app"))
}

java {
    withSourcesJar()
    configureJava()
}

kotlin {
    configureKotlin()
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
