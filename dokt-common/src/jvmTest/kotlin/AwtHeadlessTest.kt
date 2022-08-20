package app.dokt.common

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
        test("json") { Json.encodeToString(Dim.ONE) shouldBe """{"w":1,"h":1}""" }
        test("setSize") { shouldThrow<IllegalStateException> { dim.setSize(dimension) } }
        test("toString") { dim.toString() shouldBe "1 x 2" }
    }
    context("Pt") {
        context("ZERO") {
            test("json") { Json.encodeToString(Pt.ZERO) shouldBe """{"x":0,"y":0}""" }
            test("toString") { Pt.ZERO.toString() shouldBe "(0, 0)" }
        }
        context("(100, 50)") {
            val pt = Pt(100, 50)
            test("json") { Json.encodeToString(pt) shouldBe """{"x":100,"y":50}""" }
            test("toString") { pt.toString() shouldBe "(100, 50)" }
        }
    }
    context("Rect") {
        test("json") { Json.encodeToString(Rect.DOT) shouldBe """{"x":0,"y":0,"w":1,"h":1}""" }
        test("toString") { Rect.DOT.toString() shouldBe """(0, 0) 1 x 1""" }
    }
})