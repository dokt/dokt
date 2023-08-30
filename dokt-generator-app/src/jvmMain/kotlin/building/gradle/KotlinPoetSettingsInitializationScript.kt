package app.dokt.generator.building.gradle

import app.dokt.common.Version
import app.dokt.generator.REFRESH_VERSIONS_PLUGIN_ID
import app.dokt.generator.building.gradle.SettingsInitialization.Companion.FILE_NAME
import app.dokt.generator.code.beginControlFlow
import app.dokt.generator.code.controlFlow
import app.dokt.generator.code.kotlinpoet.ScriptGenerator
import app.dokt.generator.code.kotlinpoet.SuppressName
import com.squareup.kotlinpoet.CodeBlock

private const val MAVEN_LOCAL = "mavenLocal()"

private fun CodeBlock.Builder.repositories(code: CodeBlock.Builder.() -> Unit) =
    controlFlow("repositories", code = code)

class KotlinPoetSettingsInitializationScript : ScriptGenerator(FILE_NAME, {}), SettingsInitialization {
    private val pluginsDel = lazy { beginControlFlow("plugins") }
    private val plugins by pluginsDel

    private val dependencyResolutionManagementDel =
        lazy { beginControlFlow("dependencyResolutionManagement") }
    private val dependencyResolutionManagement by dependencyResolutionManagementDel

    override var root: String? = null

    override var projects: List<String> = emptyList()

    override fun pluginsUseMavenLocal() {
        file.addCode(controlFlow("pluginManagement") {
            repositories {
                addStatement("gradlePluginPortal()")
                addStatement(MAVEN_LOCAL)
            }
        })
    }

    override fun applyPlugin(id: String, version: Version) {
        if (id == REFRESH_VERSIONS_PLUGIN_ID) suppress.add(SuppressName.SpellCheckingInspection)
        plugins.addStatement("id(%S) version %S", id, version)
    }

    override fun manageDependencyResolutions(useMavenLocal: Boolean) {
        with (dependencyResolutionManagement) {
            repositories {
                addStatement("mavenCentral()")
                if (useMavenLocal) addStatement(MAVEN_LOCAL)
            }
            endControlFlow()
        }
    }

    override fun build() {
        super.build()

        if (pluginsDel.isInitialized()) file.addCode(plugins.endControlFlow().build())

        if (dependencyResolutionManagementDel.isInitialized()) file.addCode(dependencyResolutionManagement.build())

        root?.let { file.addStatement("rootProject.name = %S", it) }

        if (projects.any()) { file.addStatement("include(${projects.joinToString { "\"$it\"" }})") }
    }
}
