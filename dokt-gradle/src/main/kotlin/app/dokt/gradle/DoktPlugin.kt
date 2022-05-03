package app.dokt.gradle

import de.fayard.refreshVersions.RefreshVersionsPlugin
import isRoot
import org.gradle.api.*
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.*
import org.slf4j.LoggerFactory

/**
 * Dokt settings and project plugin.
 * See https://tomgregory.com/gradle-evaluation-order-for-multi-project-builds/
 */
class DoktPlugin : Plugin<Any> {
    private val logger = LoggerFactory.getLogger(javaClass) as Logger

    override fun apply(target: Any) {
        if (target is Project) target.configure()
        else if (target is Settings) target.initialize()
    }

    private val Project.js get() = name.endsWith("-js")
    private val Project.jvm get() = name.endsWith("-jvm")

    private fun Project.configure() {
        // TODO add projects to registry if needed
        (if (name.endsWith("-dom")) DomainProject(this)
        else if (name.endsWith("-app")) ApplicationProject(this)
        else if (name.contains("-inf")) {
            if (jvm) InfrastructureJvmProject(this)
            else if (js) InfrastructureJsProject(this)
            else InfrastructureMultiplatformProject(this)
        } else if (name.contains("-itf")) {
            if (jvm) InterfaceJvmProject(this)
            else if (js) InterfaceJsProject(this)
            else InterfaceMultiplatformProject(this)
        } else {
            if (isRoot) info("Ignoring root project")
            else warn("Unable to detect architecture layer")
            null
        })?.configureLayer()

        if (isRoot) {
            task("updateProperties") {
                dependsOn("refreshVersions")

                doFirst {
                    val propertiesFile = file("gradle.properties")
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
        quiet("Initializing on Gradle ${gradle.gradleVersion}")

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
        debug("Using Maven Central and local repositories in all projects")
        dependencyResolutionManagement {
            repositories {
                mavenCentral()
                mavenLocal()
            }
        }

        // Initialize Dokt on all projects
        debug("Initializing all projects")
        gradle.allprojects {
            quiet("Applying Dokt plugin")
            apply<DoktPlugin>()
        }
    }

    private fun debug(message: String) = logger.debug(formatDebug(message))

    private fun quiet(message: String) = logger.quiet(formatQuiet(message))

    companion object {
        private val defaultProperties = mapOf(
            "kotlin.code.style" to "official",
            "kotlin.mpp.stability.nowarn" to "true",
            "org.gradle.warning.mode" to "all"
        )
    }
}
