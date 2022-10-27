package app.dokt.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

private val json = Json { serializersModule = module }

class GeometryTest : FunSpec({
    val dimension = Dimension(3, 4)
    val point = Point(3, 4)
    context("Dimension") {
        test("area") { dimension.area shouldBe 12 }
        test("dimensionless") { dimensionless.area shouldBe 0 }
        test("FullHD in UHD") { (FullHD in UHD).shouldBeTrue() }
        test("json") { json.encodeToString(pixel) shouldBe """{"width":1,"height":1}""" }
        test("point") { dimension.point shouldBe point }
        context("ratio") {
            test("VGA") { VGA.ratio shouldBe fullscreen }
            test("StandardHD") { StandardHD.ratio shouldBe widescreen }
            test("FullHD") { FullHD.ratio shouldBe widescreen }
        }
        //test("scale") { FullHD.scale(UHD.height) shouldBe UHD }
        test("text") { CGA.text shouldBe "320 x 200" }
    }
    context("Point") {
        val originJson = """{"x":0,"y":0}"""
        test("decode") { json.decodeFromString<Point>(originJson) shouldBe origin }
        test("dimension") { point.dimension shouldBe dimension }
        test("distance") { origin.distance(point) shouldBe 5.0 }
        test("encode") { json.encodeToString(origin) shouldBe originJson }
        test("text") { origin.text shouldBe "(0, 0)" }
    }
    context("Rectangle") {
        val rectangle = Rectangle(3, 4, 100, 50)
        val rectangleJson = """{"x":3,"y":4,"width":100,"height":50}"""
        test("encode") { json.encodeToString(rectangle) shouldBe rectangleJson }
        test("decode") { json.decodeFromString<Rectangle>(rectangleJson) shouldBe rectangle }
        test("location") { rectangle.location shouldBe point }
        test("text") { rectangle.text shouldBe """(3, 4; 100 x 50)""" }
    }
})