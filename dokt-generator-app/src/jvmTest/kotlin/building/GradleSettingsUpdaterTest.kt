@file:Suppress("SpellCheckingInspection")

package app.dokt.generator.building

import app.dokt.test.linesShouldBe
import io.kotest.core.spec.style.FunSpec

@Suppress("SpellCheckingInspection")
class GradleSettingsUpdaterTest : FunSpec({
    test("add refresh versions") {
        update("""
            plugins {
                id("app.dokt) version "2.0"
            }

            $ROOT
        """.trimIndent(), """
            plugins {
                id("app.dokt) version "2.0"
                id("de.fayard.refreshVersions") version "1.0"
            }

            $ROOT
        """.trimIndent())
    }
    test("name root project") {
        update("""
            $PLUGINS
        """.trimIndent(), """
            $PLUGINS

            $ROOT
        """.trimIndent())
    }
    test("ignore root project") {
        update("""
            $PLUGINS

            rootProject.name = "bar"
        """.trimIndent())
    }
}) {
    companion object {
        const val PLUGINS = """
            plugins {
                id("app.dokt) version "2.0"
                id("de.fayard.refreshVersions") version "1.0"
            }
        """
        const val ROOT = """rootProject.name = "foo""""
        fun update(input: String, expected: String? = null) {
            GradleSettingsUpdater().update(input.lines(), "foo") linesShouldBe expected
        }
    }
}
