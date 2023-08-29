package app.dokt.generator.building.gradle

import app.dokt.generator.DOKT_SETTINGS_PLUGIN_ID
import app.dokt.generator.REFRESH_VERSIONS_PLUGIN_ID
import app.dokt.generator.vDokt
import app.dokt.generator.vRefreshVersions
import app.dokt.test.relaxed
import io.mockk.verifyAll

/** Act on the system under test */
private fun <R> sut(act: SettingsInitializer.() -> R) = SettingsInitializer.run(act)

private fun SettingsInitialization.applyPlugins() {
    applyPlugin(DOKT_SETTINGS_PLUGIN_ID, vDokt)
    applyPlugin(REFRESH_VERSIONS_PLUGIN_ID, vRefreshVersions)
}

class SettingsInitializerTest : io.kotest.core.spec.style.FunSpec({
    test("initialize()") {
        relaxed<SettingsInitialization> {
            sut { initialize() }
            verifyAll {
                applyPlugins()
            }
        }
    }

    test("initialize(true)") {
        relaxed<SettingsInitialization> {
            sut { initialize(true) }
            verifyAll {
                applyPlugins()
                configureDependencyResolutions()
            }
        }
    }

    test("initialize(useCrossProjectDependencies = true, useMavenLocal = true)") {
        relaxed<SettingsInitialization> {
            sut { initialize(useCrossProjectDependencies = true, useMavenLocal = true) }
            verifyAll {
                applyPlugins()
                configureDependencyResolutions(true)
            }
        }
    }
})
