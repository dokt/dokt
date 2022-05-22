@file:Suppress("unused", "unused_variable")

import app.dokt.gradle.*
import org.gradle.api.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsSetupTask

fun NamedDomainObjectContainer<KotlinSourceSet>.commonMain(configure: KotlinDependencyHandler.() -> Unit) {
    val commonMain by getting { dependencies { configure() } }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.commonTest(configure: KotlinDependencyHandler.() -> Unit) {
    val commonTest by getting { dependencies { configure() } }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.jvmMain(configure: KotlinDependencyHandler.() -> Unit) {
    val jvmMain by getting { dependencies { configure() } }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.jvmTest(configure: KotlinDependencyHandler.() -> Unit) {
    val jvmTest by getting { dependencies { configure() } }
}

val Project.isRoot get() = path == ":"

// https://stackoverflow.com/questions/68830854/how-to-execute-a-node-command-in-a-kotlin-js-project
private val Project.nodeDir get() = (rootProject.tasks["kotlinNodeJsSetup"] as NodeJsSetupTask).destination

val Project.npm get() = nodeDir.resolve("npm.cmd")

val Project.npx get() = nodeDir.resolve("npx.cmd")

private fun Project.configureCommonMain(configure: KotlinDependencyHandler.() -> Unit) {
    multiplatformSourceSets { commonMain { configure() } }
}

private fun Project.multiplatformSourceSets(configure: Action<NamedDomainObjectContainer<KotlinSourceSet>>) {
    configure<KotlinMultiplatformExtension> { configure.execute(sourceSets) }
}

/**
 * Configure application services project dependencies
 *
 * @param application The application project path prefix without '-app'-suffix.
 * @param domains The needed domain path prefixes without '-dom'-suffix.
 */
fun Project.applicationProject(application: String, vararg domains: String) {
    project("$application${ApplicationProject.SUFFIX}") {
        configureCommonMain {
            domains.forEach {
                api(project(":$it${DomainProject.SUFFIX}"))
            }
        }
    }
}

fun Project.applicationProject(application: String, configure: Action<NamedDomainObjectContainer<KotlinSourceSet>>) {
    project("$application${ApplicationProject.SUFFIX}") {
        multiplatformSourceSets(configure)
    }
}

fun Project.domainProject(domain: String, configure: Action<NamedDomainObjectContainer<KotlinSourceSet>>) {
    project("$domain${DomainProject.SUFFIX}") {
        multiplatformSourceSets(configure)
    }
}

/**
 * Configure domain project dependencies
 *
 * @param domain The domain project path prefix without '-dom'-suffix.
 * @param others The needed domain name prefixes without '-dom'-suffix which are in the same directory.
 */
fun Project.domainProject(domain: String, vararg others: String) {
    if (others.isNotEmpty()) {
        project("$domain${DomainProject.SUFFIX}") {
            configureCommonMain {
                others.forEach {
                    api(project(":$it${DomainProject.SUFFIX}"))
                }
            }
        }
    }
}

/**
 * Configure infrastructure project dependencies
 *
 * @param infrastructure The infrastructure project path.
 * @param commonMain Common main dependencies configurator.
 */
fun Project.infrastructureProject(infrastructure: String, commonMain: KotlinDependencyHandler.() -> Unit) {
    project(infrastructure) {
        configureCommonMain(commonMain)
    }
}
