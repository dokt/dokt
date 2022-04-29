plugins {
    `java-gradle-plugin`

    /*
        WARNING: Unsupported Kotlin plugin version.
        The `embedded-kotlin` and `kotlin-dsl` plugins rely on features of Kotlin `1.5.31`
        that might work differently than in the requested version `1.6.20-M1`.

        Gradle bundled Kotlin is old, but that doesn't cause problems. Follow issues:
        - https://github.com/gradle/gradle/issues/16345
        - https://github.com/gradle/gradle/issues/16779

        Let Gradle define the 'kotlin-dsl' version!
     */
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
    implementation(kotlin("gradle-plugin"))

    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.plugin.serialization
    implementation(kotlin("serialization"))

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
