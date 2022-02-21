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
    id("com.gradle.plugin-publish") version "0.20.0"
}

val vGradle: String by project
val vKotest: String by project
val vKotlin: String by project
val vSerialization: String by project

description = "Domain-driven design using Kotlin"

dependencies {
    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.multiplatform
    api(kotlin("gradle-plugin", vKotlin))

    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.plugin.serialization
    api(kotlin("serialization", vKotlin))

    implementation(project(":dokt-generator"))
}

tasks.processResources {
    val props = file("$buildDir/generated/dokt.properties")

    doFirst {
        props.parentFile.mkdirs()
        props.writeText("""
            vDokt=$version
            vGradle=$vGradle
            vKotest=$vKotest
            vKotlin=$vKotlin
            vSerialization=$vSerialization
            """.trimIndent())
    }

    from(props)
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
