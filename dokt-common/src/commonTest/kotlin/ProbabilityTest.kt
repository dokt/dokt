package app.dokt

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe

class ProbabilityTest : FunSpec({
    context("complement") {
        test("0.4") { forty.complement shouldBe sixty }
        test("0.5") { half.complement shouldBeEqualComparingTo half }
        test("0.6") { sixty.complement shouldBeLessThan sixty }
        test("P + P'") { sixty + sixty.complement shouldBe Probability.always }
    }
    context("exclusive and") {
        test("0.4 + 0.4") { forty + forty shouldBe eighty }
    }
    context("range") {
        test("-0.1") { shouldThrow<IllegalArgumentException> { Probability(Probability.NEVER - 0.1) } }
        test("0.0") { Probability(Probability.NEVER) }
        test("0.5") { half.value shouldBe 0.5 }
        test("1.0") { Probability(Probability.ALWAYS) }
        test("1.1") { shouldThrow<IllegalArgumentException> { Probability(Probability.ALWAYS + 0.1) } }
    }
}) {
    companion object {
        val eighty = Probability(0.8)
        val forty = Probability(0.4)
        val half = Probability(0.5)
        val sixty = Probability(0.6)

    }
}
