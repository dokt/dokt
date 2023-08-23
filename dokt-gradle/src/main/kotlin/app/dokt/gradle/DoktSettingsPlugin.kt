package app.dokt.gradle

import app.dokt.generator.building.ProjectType
import app.dokt.gradle.core.SettingsPlugin
import app.dokt.gradle.domain.DoktDomainProjectPlugin
import app.dokt.gradle.root.DoktRootProjectPlugin
import org.gradle.api.initialization.Settings
import org.gradle.util.GradleVersion

@Suppress("unused")
class DoktSettingsPlugin : SettingsPlugin(DoktSettingsPlugin::class) {
    override val minimum: GradleVersion = GradleVersion.version(MIN_GRADLE)

    /** Initialize settings. */
    override fun Settings.initialize() {
        lifecycle { "Initializing https://dokt.app" }

        debug { "Add Maven Central and local to every project repositories." }
        dependencyResolutionManagement {
            with(it.repositories) {
                mavenCentral()
                mavenLocal()
            }
        }

        debug { "Register build service and get it." }
        val service = gradle.sharedServices.registerIfAbsent(DoktBuildService.NAME, DoktBuildService::class.java) {
            it.parameters.root.set(rootDir)
        }.get()

        val projects = service.projectTypesByPath
        info {
            "Including ${projects.size} projects:\n${projects.map { (path, type) ->
                "$path ($type)"
            }.joinToString("\n")}"
        }
        include(projects.keys)

        /*debug { "Add action to root project when it's read." }
        gradle.rootProject {
            debug { "Add Dokt root project plugin." }
            it.pluginManager.apply(RootProjectPlugin::class.java)
        }*/

        debug { "Add apply plugin action before project evaluation." }
        gradle.beforeProject {
            val path = it.path
            val plugin = if (path == ":") DoktRootProjectPlugin::class
            else when (val type = projects.getValue(path)) {
                ProjectType.DOMAIN -> DoktDomainProjectPlugin::class
                else -> {
                    warn { "$type plugin not implemented!" }
                    null
                }
            }
            plugin?.run {
                lifecycle { "Applying $simpleName" }
                it.pluginManager.apply(java)
            }
        }
    }
}