package app.dokt.generator.building.gradle

import app.dokt.generator.*
import app.dokt.test.dummy
import io.mockk.*

/** Act on the system under test */
private fun <R> sut(act: SettingsInitializer.() -> R) = SettingsInitializer.run(act)

class SettingsInitializerTest : io.kotest.core.spec.style.FunSpec({
    test("applyPlugins") {
        dummy<SettingsInitialization> {
            sut { applyPlugins() }
            verifyAll {
                applyPlugin(DOKT_SETTINGS_PLUGIN_ID, vDokt)
                applyPlugin(REFRESH_VERSIONS_PLUGIN_ID, vRefreshVersions)
            }
        }
    }
})
