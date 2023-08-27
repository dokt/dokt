package app.dokt.gradle.building

import app.dokt.generator.MIN_GRADLE_VER
import app.dokt.generator.building.ProjectType
import app.dokt.gradle.app.DoktApplicationPlugin
import app.dokt.gradle.common.SettingsPlugin
import app.dokt.gradle.domain.DoktDomainPlugin
import app.dokt.gradle.iface.*
import app.dokt.gradle.infra.*
import org.gradle.api.initialization.Settings
import org.gradle.util.GradleVersion

@Suppress("unused")
class DoktSettingsPlugin : SettingsPlugin(DoktSettingsPlugin::class) {
    override val minimum: GradleVersion = GradleVersion.version(MIN_GRADLE_VER)

    /** Initialize settings. */
    override fun Settings.applyPlugin() {
        lifecycle { "Initializing https://dokt.app" }
        val extension = extensions.create("dokt", DoktExtension::class.java)
        debug { "Build File: ${extension.useBuildFile}" }

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

        debug { "Add apply plugin action before project evaluation." }
        gradle.beforeProject {
            val path = it.path
            val plugin = if (path == ":") DoktRootPlugin::class
            else when (val type = projects.getValue(path)) {
                ProjectType.APPLICATION -> DoktApplicationPlugin::class
                ProjectType.DOMAIN -> DoktDomainPlugin::class
                ProjectType.INFRASTRUCTURE -> DoktMultiInfrastructurePlugin::class
                ProjectType.INFRASTRUCTURE_JS -> DoktJsInfrastructurePlugin::class
                ProjectType.INFRASTRUCTURE_JVM -> DoktJvmInfrastructurePlugin::class
                ProjectType.INTERFACE -> DoktMultiInterfacePlugin::class
                ProjectType.INTERFACE_JS -> DoktJsInterfacePlugin::class
                ProjectType.INTERFACE_JVM -> DoktJvmInterfacePlugin::class
                ProjectType.KTOR_SERVER -> DoktKtorServerPlugin::class
                ProjectType.SWING -> DoktSwingPlugin::class
                ProjectType.SWT -> DoktSwtPlugin::class
                else -> {
                    warn { "$type plugin not implemented!" }
                    null
                }
            }
            plugin?.run {
                info { "Applying $simpleName" }
                it.pluginManager.apply(java)
            }
        }
    }
}
