package app.dokt.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CollectionsTests : FunSpec({
    context("List") {
        with((1..9).toList()) {
            test("first") { first shouldBe 1 }
            test("fourth") { fourth shouldBe 4 }
            test("last") { last shouldBe 9 }
            test("second") { second shouldBe 2 }
            test("third") { third shouldBe 3 }
        }
    }
})
