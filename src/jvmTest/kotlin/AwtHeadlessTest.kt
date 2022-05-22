package app.dokt

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.awt.Dimension

class AwtHeadlessTest : FunSpec({
    context("Dim") {
        val dim = Dim(1, 2)
        val dimension = Dimension(1, 2)
        test("getSize") { dim.size shouldBe dimension }
        test("HD < UHD") { HD shouldBeLessThan UHD }
        test("json") { Json.encodeToString(Dim.ONE) shouldBe ONE }
        test("setSize") { shouldThrow<IllegalStateException> { dim.setSize(dimension) } }
        test("toString") { dim.toString() shouldBe "Dim(width=1, height=2)" }
    }
    context("Pt") {
        test("json") { Json.encodeToString(Pt.ZERO) shouldBe "{}" }
    }
    context("Rect") {
        test("json") { Json.encodeToString(Rect.DOT) shouldBe ONE }
    }
}) {
    companion object {
        const val ONE = """{"width":1,"height":1}"""
    }
}
