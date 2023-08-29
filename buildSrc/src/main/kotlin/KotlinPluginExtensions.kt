@file:Suppress("unused")

package org.gradle.kotlin.dsl

import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun KotlinMultiplatformExtension.jvmUsingJUnitPlatform() {
    jvm {
        //withJava() TODO Is this needed in Maven artifact?
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
