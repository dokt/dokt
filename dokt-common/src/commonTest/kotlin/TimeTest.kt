package app.dokt.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.*

class TimeTest: FunSpec({
    context("Instant") {
        test("UTC") { Clock.System.now().toString().endsWith("Z")}
        val long = "2010-06-01T22:19:44.475Z"
        context("short") {
            test(long) { long.toInstant().short shouldBe "20100601T221944.475Z" }
        }
        context("shortest") {
            test(long) { long.toInstant().shortest shouldBe "20100601T221944Z" }
        }
    }
})
