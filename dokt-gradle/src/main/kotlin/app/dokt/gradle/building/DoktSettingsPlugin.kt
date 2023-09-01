package app.dokt.gradle.building

import app.dokt.common.addIf
import app.dokt.generator.building.ProjectType
import app.dokt.generator.vGradleMin
import app.dokt.gradle.applyPlugin
import app.dokt.gradle.common.SettingsPlugin
import app.dokt.gradle.plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.isRoot
import org.gradle.util.GradleVersion

private typealias Projects = Map<String, ProjectType>

@Suppress("unused")
class DoktSettingsPlugin : SettingsPlugin(DoktSettingsPlugin::class) {
    override val minimum: GradleVersion = GradleVersion.version(vGradleMin.toString())

    /** Initialize settings. */
    override fun Settings.applyPlugin() {
        lifecycle { "Initializing https://dokt.app" }

        initialize(extensions.create("dokt", DoktSettingsExtension::class.java))
    }

    private fun Settings.initialize(extension: DoktSettingsExtension) {
        debug { "Register build service and get it." }
        val service = gradle.sharedServices.registerIfAbsent(DoktBuildService.NAME, DoktBuildService::class.java) {
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
            if (inMemoryInitialization.get()) {
                lifecycle { "Performing full initialization in-memory." }

                if (useCrossProjectDependencies.get()) manageDependencyResolutions(useMavenLocal.get())
                else debug { "Repositories are configured by project." }

                val projectPaths = service.projectPaths
                debug { "Including project paths: ${projectPaths.joinToString()}." }
                include(projectPaths)

                applyPluginsFor(service.projectTypes)
            } else {
                debug { "Minimal initialization done." }
            }
        }
    }

    @Suppress("UnstableApiUsage")
    private fun Settings.manageDependencyResolutions(local: Boolean) {
        debug { "Configuring Maven Central${" and local Maven".addIf(local)}} to cross-project dependencies." }
        dependencyResolutionManagement {
            with(it.repositories) {
                mavenCentral()
                if (local) mavenLocal()
            }
        }
    }

    private fun Settings.applyPluginsFor(projects: Projects) {
        debug { "Add apply plugin action before subproject evaluation." }
        gradle.beforeProject {
            if (!it.isRoot) projects.getValue(it.path).plugin.run {
                info { "Applying $simpleName" }
                it.pluginManager.apply(java)
            }
        }
    }
}
