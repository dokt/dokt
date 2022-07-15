package app.dokt.generator.building

import app.dokt.generator.kotlinpoet.shouldCode
import io.kotest.core.spec.style.FunSpec

class GradleSettingsWriterTest : FunSpec({
    val instance = GradleSettingsWriter(GradleProjectTest.instance)
    test("generateScript") {
        instance.generateScript() shouldCode """
            pluginManagement {
              repositories {
                gradlePluginPortal()
                mavenLocal()
              }
            }
            plugins {
              id("de.fayard.refreshVersions") version "0.40.2"
            }
            rootProject.name = "examples"
            
            // Dokt libraries
            include("dokt-application", "dokt-common", "dokt-domain", "dokt-domain-test", "dokt-interface",
                "dokt-test")
            
            // Domain architecture layer projects
            include("erp-dom", "file-dom", "hash-dom", "hello-dom")
            
            // Application architecture layer projects
            include("window-simulator-app")
            
            // Interface architecture layer projects
            include("window-simulator-swing")
            """
    }

    test("write") { instance.write() }
})