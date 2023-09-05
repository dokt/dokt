package app.dokt.gradle.building

import app.dokt.common.addIf
import app.dokt.generator.GRADLE_VER
import app.dokt.generator.building.ProjectType
import app.dokt.gradle.applyPlugin
import app.dokt.gradle.common.SettingsPlugin
import app.dokt.gradle.plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.isRoot

@Suppress("unused")
class DoktSettingsPlugin : SettingsPlugin(DoktSettingsPlugin::class) {
    override val minimum = GRADLE_VER

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
            if (inMemoryInitialization.getOrElse(false)) {
                lifecycle { "Performing full initialization in-memory." }

                if (useCrossProjectDependencies.getOrElse(false))
                    manageDependencyResolutions(useMavenLocal.getOrElse(false))
                else debug { "Repositories are configured by project." }

                val subprojectsToInclude = service.subprojectsToInclude
                debug { "Including projects: ${subprojectsToInclude.joinToString()}." }
                include(subprojectsToInclude)

                applyPluginsFor(service.subprojectTypesByPath)
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

    private fun Settings.applyPluginsFor(subprojectTypesByPath: Map<String, ProjectType>) {
        debug { "Add apply plugin action before subproject evaluation." }
        gradle.beforeProject {
            if (!it.isRoot) subprojectTypesByPath.getValue(it.path).plugin.run {
                info { "Applying $simpleName" }
                it.pluginManager.apply(java)
            }
        }
    }
}
