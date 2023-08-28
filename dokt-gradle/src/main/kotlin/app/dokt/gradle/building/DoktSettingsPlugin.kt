package app.dokt.gradle.building

import app.dokt.generator.building.ProjectType
import app.dokt.generator.vGradleMin
import app.dokt.gradle.common.SettingsPlugin
import app.dokt.gradle.plugin
import org.gradle.api.initialization.Settings
import org.gradle.util.GradleVersion

private typealias Projects = Map<String, ProjectType>

@Suppress("unused")
class DoktSettingsPlugin : SettingsPlugin(DoktSettingsPlugin::class) {
    override val minimum: GradleVersion = GradleVersion.version(vGradleMin.toString())

    /** Initialize settings. */
    override fun Settings.applyPlugin() {
        lifecycle { "Initializing https://dokt.app" }
        val extension = extensions.create("dokt", DoktSettingsExtension::class.java)
        debug { "Build File: ${extension.useBuildFile}" }

        configureDependencyResolutions()

        debug { "Register build service and get it." }
        val buildService = gradle.sharedServices.registerIfAbsent(DoktBuildService.NAME, DoktBuildService::class.java) {
            it.parameters.root.set(rootDir)
        }.get()

        val projects = includeProjects(buildService)

        applyPluginsFor(projects)
    }

    private fun Settings.applyPluginsFor(projects: Projects) {
        debug { "Add apply plugin action before project evaluation." }
        gradle.beforeProject {
            val path = it.path
            if (path == ":") ProjectType.ROOT.plugin else projects.getValue(path).plugin.run {
                info { "Applying $simpleName" }
                it.pluginManager.apply(java)
            }
        }
    }

    private fun Settings.configureDependencyResolutions() {
        debug { "Add Maven Central and local to every project repositories." }
        dependencyResolutionManagement {
            with(it.repositories) {
                mavenCentral()
                mavenLocal()
            }
        }
    }

    private fun Settings.includeProjects(buildService: DoktBuildService) : Projects {
        val projects = buildService.projectTypesByPath
        info {
            "Including ${projects.size} projects:\n${projects.map { (path, type) ->
                "$path ($type)"
            }.joinToString("\n")}"
        }
        include(projects.keys)
        return projects
    }
}
