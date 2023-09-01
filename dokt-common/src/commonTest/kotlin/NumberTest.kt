package app.dokt.common

import app.dokt.test.c
import app.dokt.test.impl.Case2
import app.dokt.test.shouldBeTrue
import app.dokt.test.tests
import app.dokt.test.testsWithMatcher
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

private fun i(a: IntRange) = Case2(a, 1..<5, " ∩ ")

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

    context("ClosedRange") {
        test("single") { (1..1).single.shouldBeTrue() }
        test("range") { (1..2).single.shouldBeFalse() }
    }

    context("OpenEndedRange") {
        // Other range is always 1..<5

        testsWithMatcher("intersects true", { shouldBeTrue() },
            i(0..<2),
            i(1..<3),
            i(2..<4),
            i(3..<5),
            i(4..<6),
            i(1..<5),
            i(-1..<7),
            i(-2..<8)
        ) { (a, b) ->
            val result = a.intersects(b)
            b.intersects(a) shouldBe result
            result
        }

        testsWithMatcher("intersects false", { shouldBeFalse() },
            i(-2..<0),
            i(-1..<1),
            i(5..<7),
            i(6..<8)
        ) { (a, b) ->
            val result = a.intersects(b)
            b.intersects(a) shouldBe result
            result
        }
    }
})
