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
        useCrossProjectDependencies: Boolean = false,
        useMavenLocal: Boolean = false
    ) {
        debug { "Applying Dokt settings plugin." }
        applyPlugin(DOKT_SETTINGS_PLUGIN_ID, vDokt)

        debug { "Applying refreshVersions settings plugin." }
        applyPlugin(REFRESH_VERSIONS_PLUGIN_ID, vRefreshVersions)

        if (useCrossProjectDependencies) {
            debug {
                "Configuring Maven Central${" and local Maven".addIf(useMavenLocal)}} to cross-project dependencies."
            }
            configureDependencyResolutions(useMavenLocal)
        } else {
            debug { "Repositories are configured by project." }
        }
    }
}
