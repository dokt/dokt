package app.dokt.gradle.building

import app.dokt.common.Version
import app.dokt.generator.building.ProjectType
import app.dokt.generator.building.gradle.SettingsInitialization
import app.dokt.generator.building.gradle.SettingsInitializer.initialize
import app.dokt.generator.vGradleMin
import app.dokt.gradle.applyPlugin
import app.dokt.gradle.common.SettingsPlugin
import app.dokt.gradle.plugin
import org.gradle.api.initialization.Settings
import org.gradle.util.GradleVersion

private typealias Projects = Map<String, ProjectType>

@Suppress("unused")
class DoktSettingsPlugin : SettingsPlugin(DoktSettingsPlugin::class), SettingsInitialization {
    override val minimum: GradleVersion = GradleVersion.version(vGradleMin.toString())

    private lateinit var buildService: DoktBuildService

    override var root: String? = null

    override var projects: List<String>
        get() = buildService.projectTypesByPath.keys.toList()
        set(value) {
            lifecycle { "Including project paths: ${value.joinToString()}." }
            settings.include(value)
        }

    /** Initialize settings. */
    override fun Settings.applyPlugin() {
        lifecycle { "Initializing https://dokt.app" }

        initialize(extensions.create("dokt", DoktSettingsExtension::class.java))
    }

    private fun Settings.initialize(extension: DoktSettingsExtension) {
        debug { "Register build service and get it." }
        buildService = gradle.sharedServices.registerIfAbsent(DoktBuildService.NAME, DoktBuildService::class.java) {
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
                    root,
                    projects,
                    useMavenLocal.get(),
                    useCrossProjectDependencies.get()
                )

                applyPluginsFor(buildService.projectTypesByPath)
            }
        }
    }

    override fun pluginsUseMavenLocal() {
        debug { "Plugin can't manage plugins." }
    }

    override fun applyPlugin(id: String, version: Version) {
        debug { "Ignoring $id settings plugin apply, because unable set its version $version." }
    }

    @Suppress("UnstableApiUsage")
    override fun manageDependencyResolutions(useMavenLocal: Boolean) {
        settings.dependencyResolutionManagement {
            with(it.repositories) {
                mavenCentral()
                if (useMavenLocal) mavenLocal()
            }
        }
    }

    private fun Settings.applyPluginsFor(projects: Projects) {
        debug { "Add apply plugin action before project evaluation." }
        gradle.beforeProject {
            projects.getValue(it.path).plugin.run {
                info { "Applying $simpleName" }
                it.pluginManager.apply(java)
            }
        }
    }
}
