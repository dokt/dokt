package app.dokt.generator.building

import app.dokt.generator.code.*
import com.squareup.kotlinpoet.*

/**
 * Gradle Kotlin settings script updater
 *
 * TODO update functionality
 */
class GradleSettingsWriter(private val root: GradleProject) : KotlinScriptWriter() {
    override val directory = root.dir

    override val name = "settings.gradle"

    override fun FileSpec.Builder.generateScript() {
        addCode(generatePluginManagement())
        addCode(generatePlugins())
        if (CENTRALIZED_REPOSITORY_DECLARATION) addCode(generateDependencyResolutionManagement())
        addStatement("rootProject.name = %S", root.name)
        if (Dokt.LOCAL) {
            addCode("\n")
            addBodyComment("Dokt libraries")
            addStatement("include(%L)", Dokt.values().joinToString { "\"${it.artifact}\"" })
        }
        generateInclude(Layer.DOMAIN)
        generateInclude(Layer.APPLICATION)
        generateInclude(Layer.INFRASTRUCTURE)
        generateInclude(Layer.INTERFACE)
    }

    private fun FileSpec.Builder.generateInclude(layer: Layer): FileSpec.Builder {
        val projects = root.descendants.filter { it.layer == layer }
        if (projects.isEmpty()) return this
        addCode("\n")
        addBodyComment("$layer projects")
        return addStatement("include(${projects.joinToString { "\"${it.name}\"" }})")
    }

    companion object {
        /**
         * https://docs.gradle.org/current/userguide/dependency_management.html#sub:centralized-repository-declaration
         *
         * TODO might not work
         */
        const val CENTRALIZED_REPOSITORY_DECLARATION = true

        private const val REFRESH_VERSION = "0.40.2"

        private fun generateDependencyResolutionManagement() = controlFlow("dependencyResolutionManagement") {
            controlFlow("repositories") {
                addStatement("mavenCentral()")
                addStatement("mavenLocal()")
            }
        }

        private fun generatePluginManagement() = controlFlow("pluginManagement") {
            controlFlow("repositories") {
                addStatement("gradlePluginPortal()")
                addStatement("mavenLocal()")
            }
        }

        private fun generatePlugins() = controlFlow("plugins") {
            addStatement("id(%S) version %S", "de.fayard.refreshVersions", REFRESH_VERSION)
        }
    }
}