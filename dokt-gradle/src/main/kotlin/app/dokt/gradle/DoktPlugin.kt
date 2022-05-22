package app.dokt.gradle

import de.fayard.refreshVersions.RefreshVersionsPlugin
import isRoot
import org.gradle.api.*
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.api.logging.Logger
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.plugin.KotlinJsPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.slf4j.LoggerFactory

/**
 * Dokt settings and project plugin.
 * See https://tomgregory.com/gradle-evaluation-order-for-multi-project-builds/
 */
@Suppress("unused")
class DoktPlugin : Plugin<Any> {
    private val logger = LoggerFactory.getLogger(javaClass) as Logger

    override fun apply(target: Any) {
        if (target is Project) target.configure()
        else if (target is Settings) target.initialize()
    }

    private val Project.js get() = path.contains("js")
    private val Project.jvm get() = path.contains("jvm")

    private fun Project.configure() {
        val srcDir = projectDir.resolve("src")
        if (srcDir.exists()) {
            // TODO add projects to registry if needed
            (if (name.endsWith(DomainProject.SUFFIX)) DomainProject(this, srcDir)
            else if (name.endsWith(ApplicationProject.SUFFIX)) ApplicationProject(this, srcDir)
            else if (name.contains(InterfaceProject.TAG)) {
                if (jvm) InterfaceJvmProject(this, srcDir)
                else if (js) InterfaceJsProject(this, srcDir)
                else InterfaceMultiplatformProject(this, srcDir)
            } else {
                if (jvm) InfrastructureJvmProject(this, srcDir)
                else if (js) InfrastructureJsProject(this, srcDir)
                else InfrastructureMultiplatformProject(this, srcDir)
            }).configureLayer()
        } else if (buildFile.exists()) {
            // Enable Kotlin plugins for build script.
            if (jvm) apply<KotlinPluginWrapper>()
            else if (js) apply<KotlinJsPluginWrapper>()
            else apply<KotlinMultiplatformPluginWrapper>()
        }

        if (isRoot) {
            task("updateProperties") {
                dependsOn("refreshVersions")

                doFirst {
                    val propertiesFile = file(Project.GRADLE_PROPERTIES)
                    val properties = if (propertiesFile.exists()) {
                        val existing = propertiesFile.readLines()
                            .filterNot { it.isBlank() || it.startsWith('#') }
                            .associate { line -> line.split("=").let { it[0].trim() to it[1].trim() } }
                            .toMutableMap()
                        existing += defaultProperties
                        existing.toSortedMap()
                    } else defaultProperties
                    propertiesFile.writeText(properties.toSortedMap()
                        .map { "${it.key}=${it.value}" }
                        .joinToString("\n")
                    )

                    // Configuring Gradle Wrapper
                    if (buildFile.exists() && !buildFile.readText().contains("wrapper")) buildFile.appendText(
                            "\ntasks.wrapper { distributionType = Wrapper.DistributionType.ALL }\n"
                    )
                }
            }
        }
    }

    private fun Settings.initialize() {
        quiet("Initializing Dokt $VER on Gradle ${gradle.gradleVersion}")

        /*pluginManagement {
            repositories {
                gradlePluginPortal()
                mavenLocal()
            }
        }*/

        // Applying https://jmfayard.github.io/refreshVersions
        debug("Applying Refresh Versions plugin")
        apply<RefreshVersionsPlugin>()

        // Define repositories for all projects
        // https://docs.gradle.org/current/userguide/declaring_repositories.html#sub:centralized-repository-declaration
        // TODO Doesn't work in all projects
        /** debug("Using Maven Central and local repositories in all projects")
        dependencyResolutionManagement {
            repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
            repositories {
                mavenCentral()
                mavenLocal()
            }
        }*/

        // Initialize Dokt on all projects
        debug("Initializing all projects")
        gradle.allprojects {
            debug("Applying Dokt plugin")
            apply<DoktPlugin>()
        }
    }

    private fun debug(message: String) = logger.debug(formatDebug(message))

    private fun quiet(message: String) = logger.quiet(formatQuiet(message))

    companion object {
        /** Kotlin plugin ID prefix */
        const val KOTLIN = "org.jetbrains.kotlin"
        const val VER = "0.3.0-SNAPSHOT+8"

        private val defaultProperties = mapOf(
            "kotlin.code.style" to "official",
            "kotlin.mpp.stability.nowarn" to "true",
            "org.gradle.warning.mode" to "all"
        )
    }
}
