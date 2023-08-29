@file:Suppress("unused")

package org.gradle.kotlin.dsl

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

/** Defines project defaults: group, version, description and Maven Central repository. */
fun Project.setDoktDefaults(version: String, description: String) {
    group = "app.dokt"
    this.version = version
    this.description = description

    repositories {
        mavenCentral()
    }
}

fun Project.mavenCentral() = repositories {
    mavenCentral()
}

fun KotlinJvmProjectExtension.configureKotlin() {
    compilerOptions {
        configureCompiler()
    }
}

fun KotlinMultiplatformExtension.configureJvm() {
    jvm {
        //withJava() TODO Is this needed in Maven artifact?

        compilations.all {
            compilerOptions.configure {
                configureCompiler()
            }
        }
    }
}

fun KotlinMultiplatformExtension.configureJvmWithTests() {
    jvm {
        //withJava() TODO Is this needed in Maven artifact?

        compilations.all {
            compilerOptions.configure {
                configureCompiler()
            }
        }

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.commonMainDependencies(configure: KotlinDependencyHandler.() -> Unit) {
    val commonMain by getting {
        dependencies {
            configure()
        }
    }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.commonTestDependencies(configure: KotlinDependencyHandler.() -> Unit) {
    val commonTest by getting {
        dependencies {
            configure()
        }
    }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.jvmMainDependencies(configure: KotlinDependencyHandler.() -> Unit) {
    val jvmMain by getting {
        dependencies {
            configure()
        }
    }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.jvmTestDependencies(configure: KotlinDependencyHandler.() -> Unit) {
    val jvmTest by getting {
        dependencies {
            configure()
        }
    }
}
