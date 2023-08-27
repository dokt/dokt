package app.dokt.generator.building.gradle

import app.dokt.generator.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun String.sut(act: PsiSettingsInitializationScript.() -> Unit) =
    PsiSettingsInitializationScript(kts).apply { act() }.content

class PsiSettingsScriptInitializationTest : FunSpec({
    test("applyPlugin") {
        incomplete.sut {
            applyPlugin(DOKT_SETTINGS_PLUGIN_ID, vDokt)
            applyPlugin(REFRESH_VERSIONS_PLUGIN_ID, vRefreshVersions)
        } shouldBe complete
    }
})

private val incomplete = """
    @file:Suppress("SpellCheckingInspection")

    pluginManagement {
      repositories {
        gradlePluginPortal()
        mavenLocal()
      }
    }

    plugins {
      id("app.dokt") version "$vDokt"
    }

    dokt {
      useBuildFile.set(true)
    }

    rootProject.name = "example"
""".trimIndent()

private val complete = """
    @file:Suppress("SpellCheckingInspection")

    pluginManagement {
      repositories {
        gradlePluginPortal()
        mavenLocal()
      }
    }

    plugins {
      id("app.dokt") version "$vDokt"
      id("de.fayard.refreshVersions") version "$vRefreshVersions"
    }

    dokt {
      useBuildFile.set(true)
    }

    rootProject.name = "example"
""".trimIndent()
