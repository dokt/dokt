package app.dokt.gradle.building

import app.dokt.common.Version
import app.dokt.generator.building.ProjectType
import app.dokt.generator.building.gradle.SettingsInitialization
import app.dokt.generator.building.gradle.SettingsInitializer.initialize
import app.dokt.generator.vGradleMin
import app.dokt.gradle.applyPlugin
import app.dokt.gradle.common.SettingsPlugin
import app.dokt.gradle.plugin
import isRoot
import org.gradle.api.initialization.Settings
import org.gradle.util.GradleVersion

private typealias Projects = Map<String, ProjectType>

@Suppress("unused")
class DoktSettingsPlugin : SettingsPlugin(DoktSettingsPlugin::class), SettingsInitialization {
    override val minimum: GradleVersion = GradleVersion.version(vGradleMin.toString())

    /** Initialize settings. */
    override fun Settings.applyPlugin() {
        lifecycle { "Initializing https://dokt.app" }

        initialize(extensions.create("dokt", DoktSettingsExtension::class.java))
    }

    fun Settings.initialize(extension: DoktSettingsExtension) {
        debug { "Register build service and get it." }
        val buildService = gradle.sharedServices.registerIfAbsent(DoktBuildService.NAME, DoktBuildService::class.java) {
            it.parameters.apply {
                root.set(rootDir)
                settings.set(extension)
            }
        }.get()

        debug { "Always ensure that at least a root plugin is applied." }
        gradle.rootProject {
            it.applyPlugin<DoktRootPlugin>()
        }

        with (extension) {
            if (useOnlyBuildFile.get()) {
                debug { "Using only the settings file to initialize the settings." }
            } else {
                debug { "Adding own initializations in addition to the settings file." }
                initialize(
                    useCrossProjectDependencies.get(),
                    useMavenLocal.get()
                )
            }
        }

        val projects = includeProjects(buildService)

        applyPluginsFor(projects)
    }

    override fun applyPlugin(id: String, version: Version) {
        warn { "Applying $id settings plugin, but unable specify its version $version!" }
        settings.pluginManager.apply(id)
    }

    @Suppress("UnstableApiUsage")
    override fun configureDependencyResolutions(useMavenLocal: Boolean) {
        settings.dependencyResolutionManagement {
            with(it.repositories) {
                debug { "Adding Maven Central to cross-project repositories." }
                mavenCentral()

                if (useMavenLocal) {
                    debug { "Adding local Maven to cross-project repositories." }
                    mavenLocal()
                }
            }
        }
    }

    private fun Settings.applyPluginsFor(projects: Projects) {
        debug { "Add apply plugin action before project evaluation." }
        gradle.beforeProject {
            if (!it.isRoot) projects.getValue(it.path).plugin.run {
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
