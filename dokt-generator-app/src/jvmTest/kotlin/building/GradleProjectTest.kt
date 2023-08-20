package app.dokt.generator.building

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.*
import io.kotest.matchers.shouldBe
import java.nio.file.Path

class GradleProjectTest : FunSpec({
    context("exports") {
        test("erp-dom") { "erp-dom" shouldExport listOf("com.corex.erp.shared", "com.corex.erp.so") }
        test("hello-dom") { instance["hello-dom"].exports shouldBe emptyList() }
        test("window-simulator-app") { "window-simulator-app" shouldExport listOf("fi.papinkivi.simulator") }
        test("window-simulator-swing") { "window-simulator-swing" shouldExport listOf("fi.papinkivi.simulator") }
    }
    context("imports") {
        test("erp-dom") {
            "erp-dom" shouldImport listOf("app.dokt.domain", "com.corex.erp.shared", "kotlinx.serialization")
        }
        test("hello-dom") { "hello-dom" shouldImport listOf("app.dokt.domain", "kotlinx.serialization") }
        test("window-simulator-app") {
            "window-simulator-app" shouldImport listOf("app.dokt.app", "com.sun.jna.platform")
        }
        test("window-simulator-swing") { "window-simulator-swing" shouldImport listOf(
            "app.dokt.ui.swing",
            "java.awt.event",
            "java.util",
            "javax.swing",
            "javax.swing.table",
            "jiconfont.icons.google_material_design_icons"
        ) }
    }
    test("all") { instance.all shouldHaveSize 8 }
    test("applications") { GradleProject.applications shouldHaveSize 1 }
    test("domains") { GradleProject.domains shouldHaveSize 5 }
    test("infrastructures") { GradleProject.infrastructures shouldHaveSize 0 }
    test("interfaces") { GradleProject.interfaces shouldHaveSize 1 }
}) {
    companion object {
        val instance = GradleProject.parse(Path.of("..", "examples"))

        private infix fun String.shouldExport(expected: List<String>) =
            instance[this].exports shouldContainInOrder expected

        private infix fun String.shouldImport(expected: List<String>) =
            instance[this].imports shouldContainInOrder expected
    }
}