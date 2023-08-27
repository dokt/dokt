package app.dokt.generator.building.psi

import app.dokt.common.Version
import app.dokt.generator.code.psi.Psi
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GradleSettingsScriptPsiUpdaterTest : FunSpec({
    test("addPlugin") {
        example.instance.apply { addPlugin("foo", v1) }.content shouldBe example
    }
})

private val example = """
    @file:Suppress("SpellCheckingInspection")

    pluginManagement {
      repositories {
        gradlePluginPortal()
        mavenLocal()
      }
    }

    plugins {
      id("app.dokt") version "0.2.10"
      id("de.fayard.refreshVersions") version "0.51.0"
    }

    dokt {
      useBuildFile.set(true)
    }

    rootProject.name = "examples"
""".trimIndent()

private val v1 = Version()
private val String.instance get() = GradleSettingsScriptPsiUpdater(Psi.parseKts(this))
