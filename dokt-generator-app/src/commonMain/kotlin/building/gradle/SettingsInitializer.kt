package app.dokt.generator.building.gradle

import app.dokt.app.ApplicationService
import app.dokt.common.addIf
import app.dokt.generator.DOKT_SETTINGS_PLUGIN_ID
import app.dokt.generator.REFRESH_VERSIONS_PLUGIN_ID
import app.dokt.generator.vDokt
import app.dokt.generator.vRefreshVersions
import app.dokt.infra.Logger

@ApplicationService
object SettingsInitializer : Logger({}) {
    fun SettingsInitialization.initialize(
        root: String? = null,
        projects: List<String> = emptyList(),
        local: Boolean = false,
        crossProject: Boolean = false,
    ) {
        if (local) {
            debug { "Manage plugins to use Maven local repository." }
            pluginsUseMavenLocal()
        }

        debug { "Applying Dokt settings plugin." }
        applyPlugin(DOKT_SETTINGS_PLUGIN_ID, vDokt)

        debug { "Applying refreshVersions settings plugin." }
        applyPlugin(REFRESH_VERSIONS_PLUGIN_ID, vRefreshVersions)

        if (crossProject) {
            debug {
                "Configuring Maven Central${" and local Maven".addIf(local)}} to cross-project dependencies."
            }
            manageDependencyResolutions(local)
        } else {
            debug { "Repositories are configured by project." }
        }

        root?.let {
            debug { "Settings root project name to '$root'." }
            this.root = it
        }

        if (projects.any()) {
            debug { "Including projects: ${projects.joinToString()}." }
            this.projects = projects
        }
    }
}
