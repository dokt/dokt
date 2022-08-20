package app.dokt.common

import app.dokt.test.c
import app.dokt.test.tests
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NumberTest : FunSpec({
    context("Double") {
        context("roundAwayFromZero") {
            test("-.5") { (-0.5).roundAwayFromZero shouldBe -1 }

            test(".5") { .5.roundAwayFromZero shouldBe 1 }
        }
    }

    context("Float") {
        tests("round2",
            c(0f, 0f),
            c(1.234f, 1.23f),
            c(8.765f, 8.77f)
        ) { (a, b) -> a.round2 shouldBe b }
    }
})
