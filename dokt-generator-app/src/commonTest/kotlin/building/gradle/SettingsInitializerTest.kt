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

    test("initialize(null, emptyList(), true)") {
        relaxed<SettingsInitialization> {
            sut { initialize(null, emptyList(), true) }
            verifyAll {
                pluginsUseMavenLocal()
                applyPlugins()
            }
        }
    }

    test("initialize(null, emptyList(), false, true)") {
        relaxed<SettingsInitialization> {
            sut { initialize(null, emptyList(), local = false, crossProject = true) }
            verifyAll {
                applyPlugins()
                manageDependencyResolutions()
            }
        }
    }

    test("initialize(null, emptyList(), true, true)") {
        relaxed<SettingsInitialization> {
            sut { initialize(null, emptyList(), local = true, crossProject = true) }
            verifyAll {
                pluginsUseMavenLocal()
                applyPlugins()
                manageDependencyResolutions(true)
            }
        }
    }

    test("initialize(foo)") {
        relaxed<SettingsInitialization> {
            sut { initialize("foo") }
            verifyAll {
                applyPlugins()
                root = "foo"
            }
        }
    }

    test("initialize(null, list)") {
        relaxed<SettingsInitialization> {
            val list = listOf("foo", "bar")
            sut { initialize(null, list) }
            verifyAll {
                applyPlugins()
                projects = list
            }
        }
    }
})
