package app.dokt.generator.building.gradle

import app.dokt.generator.DOKT_SETTINGS_PLUGIN_ID
import app.dokt.generator.REFRESH_VERSIONS_PLUGIN_ID
import app.dokt.generator.vDokt
import app.dokt.generator.vRefreshVersions
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun sut(act: KotlinPoetSettingsInitializationScript.() -> Unit) = KotlinPoetSettingsInitializationScript()
    .apply(act).content

class KotlinPoetSettingsInitializationScriptTest : FunSpec({
    test("pluginsUseMavenLocal()") {
        sut { pluginsUseMavenLocal() } shouldBe """
            pluginManagement {
                repositories {
                    gradlePluginPortal()
                    mavenLocal()
                }
            }
        """.trimIndent()
    }

    test("applyPlugin(DOKT)") {
        sut { applyPlugin(DOKT_SETTINGS_PLUGIN_ID, vDokt) } shouldBe """
            plugins {
                id("$DOKT_SETTINGS_PLUGIN_ID") version "$vDokt"
            }
        """.trimIndent()
    }

    test("applyPlugin(REFRESH_VERSIONS)") {
        sut { applyPlugin(REFRESH_VERSIONS_PLUGIN_ID, vRefreshVersions) } shouldBe """
            @file:Suppress("SpellCheckingInspection")

            plugins {
                id("$REFRESH_VERSIONS_PLUGIN_ID") version "$vRefreshVersions"
            }
        """.trimIndent()
    }

    test("manageDependencyResolutions()") {
        sut { manageDependencyResolutions() } shouldBe """
            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                }
            }
        """.trimIndent()
    }

    test("manageDependencyResolutions(true)") {
        sut { manageDependencyResolutions(true) } shouldBe """
            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                    mavenLocal()
                }
            }
        """.trimIndent()
    }

    test("rootProject = foo") {
        sut { root = "foo" } shouldBe """rootProject.name = "foo""""
    }

    test("projects = list") {
        sut { projects = listOf(":foo", ":bar") } shouldBe """include(":foo", ":bar")"""
    }
})
