package app.dokt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StringTest : FunSpec({
    with("F1a") {
        test("first") { first shouldBe 'F' }

        test("second") { second shouldBe '1' }

        test("secondInt") { secondInt shouldBe 1 }

        test("third") { third shouldBe 'a' }
    }

    context("joinWithSpace") {
        with(setOf(1, 2)) {
            test("default") { joinWithSpace() shouldBe "1 2" }

            test("transform") { joinWithSpace { "${it + 1}" } shouldBe "2 3" }
        }
    }
})
