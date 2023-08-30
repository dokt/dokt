package app.dokt.generator.building

import app.dokt.generator.kotlinpoet.shouldCode
import io.kotest.core.spec.style.FunSpec

class GradleSettingsWriterTest : FunSpec({
    val instance = GradleSettingsWriter(GradleProjectTest.instance)
    xtest("generateScript") {
        instance.createModel().build() shouldCode """
            pluginManagement {
              repositories {
                gradlePluginPortal()
                mavenLocal()
              }
            }
            plugins {
              id("de.fayard.refreshVersions") version "0.50.2"
            }
            dependencyResolutionManagement {
              repositories {
                mavenCentral()
                mavenLocal()
              }
            }
            rootProject.name = "examples"

            // Domain architecture layer projects
            include("erp-dom", "file-dom", "hash-dom", "hello-dom", "window-dom")

            // Application architecture layer projects
            include("window-simulator-app")

            // Interface architecture layer projects
            include("window-simulator-swing")
            """
    }

    xtest("write") { instance.generate() }
})
