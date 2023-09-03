package app.dokt.generator.building.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

private fun sut(text: String, act: SettingsFileUpdater.() -> Unit) = SettingsFileUpdater(text).apply(act).content

class SettingsFileUpdaterTest : FunSpec({
    context("plugins") {
        val minimal = """
            plugins {
                $APPLY_DOKT
                $APPLY_REFRESH
            }

        """.trimIndent()
        test("empty") { sut("") { applyPlugins().shouldBeTrue() } shouldBe minimal }
        test("minimal") { sut(minimal) { applyPlugins().shouldBeFalse() } shouldBe minimal }
        val barPlugin = """id("bar.plugin") version "1.0.0""""
        val custom = """
            plugins {
                $APPLY_DOKT
                $barPlugin
                $APPLY_REFRESH
            }

        """.trimIndent()
        test("custom") { sut(custom) { applyPlugins().shouldBeFalse() } shouldBe custom }
    }

    context("dependencyResolutionManagement") {
        val dep = "dependencyResolutionManagement"
        val centralOnly = """
            $dep {
                repositories {
                    mavenCentral()
                }
            }

        """.trimIndent()
        test("empty, no local") { sut("") {
            manageDependencyResolutions(false).shouldBeTrue() } shouldBe centralOnly }
        val withLocal = """
            $dep {
                repositories {
                    mavenCentral()
                    mavenLocal()
                }
            }

        """.trimIndent()
        test("empty, local") { sut("") {
            manageDependencyResolutions(true).shouldBeTrue() } shouldBe withLocal }
        test("addLocal") { sut(centralOnly) {
            manageDependencyResolutions(true).shouldBeTrue() } shouldBe "$withLocal\n" }
        test("central, no change") { sut(centralOnly) {
            manageDependencyResolutions(false).shouldBeFalse() } shouldBe centralOnly }
        test("local, no change") { sut(withLocal) {
            manageDependencyResolutions(true).shouldBeFalse() } shouldBe withLocal }
        test("no repositories, central") { sut("$dep {}") {
            manageDependencyResolutions(false).shouldBeTrue() } shouldBe centralOnly }
        test("no repositories, local") { sut("$dep {}") {
            manageDependencyResolutions(true).shouldBeTrue() } shouldBe withLocal }
    }

    context("rootProject") {
        val foo = "rootProject.name = \"foo\"\n"
        test("empty") { sut("") { rootProject("foo").shouldBeTrue() } shouldBe foo }
        test("exist") { sut(foo) { rootProject("bar").shouldBeFalse() } shouldBe foo }
    }

    context("include") {
        val foo = "include(\":foo\")\n"
        test("empty") { sut("") { include(listOf(":foo")).shouldBeTrue() } shouldBe foo }
        test("single") { sut(foo) { include(listOf(":foo")).shouldBeFalse() } shouldBe foo }
        test("multiple includes") { sut("""
            include(":foo")
            include(":bar", ":baz")

        """.trimIndent()) {
            include(listOf(":foo", ":baz", ":bax")).shouldBeTrue()
        } shouldBe """
            include(":foo")
            include(":bar", ":baz")
            include(":bax")

        """.trimIndent() }
    }

    context("update") {
        test("empty") { sut("") {
            SettingsInitialization(cross = true, local = true, "foo", listOf(":bar")).update().shouldBeTrue()
        } shouldBe """
            plugins {
                id("app.dokt") version "0.2.10"
                id("de.fayard.refreshVersions") version "0.60.2"
            }
            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                    mavenLocal()
                }
            }
            rootProject.name = "foo"
            include(":bar")

        """.trimIndent() }
    }
})
