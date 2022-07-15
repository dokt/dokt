@file:Suppress("unused")

package app.dokt.test

import app.dokt.test.impl.*
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.core.test.TestContext
import io.kotest.matchers.comparables.*
import io.kotest.matchers.floats.*

fun <A : Any?, B: Any?> c(a: A, b: B) = Case2(a, b)

fun <A : Any?, B: Any?, C: Any?> c(a: A, b: B, c: C) = Case3(a, b, c)

infix fun Float.shouldBePercent(expected: Int) {
    val percentage = expected / 100f
    val min = percentage - 0.005f
    val max = percentage + 0.005f
    withClue("expected:<$min..$max> but was:<$this>") {
        shouldBeGreaterThan(min)
        shouldBeLessThan(max)
    }
}

infix fun Float.shouldBeRoughly(expected: Double) = shouldBeRoughly(expected.toFloat())

infix fun Float.shouldBeRoughly(expected: Float) = shouldBeWithinPercentageOf(expected, 0.5)

infix fun <T : Comparable<T>> T.shouldBeInRange(range: ClosedRange<T>) {
    this shouldBeGreaterThanOrEqualTo range.start
    this shouldBeLessThanOrEqualTo range.endInclusive
}

fun <T : Any> FunSpec.test(
    name: String,
    cases: Iterable<T>,
    test: suspend TestContext.(T) -> Unit
) {
    context(name) {
        cases.forEach {
            test(it.testName) {
                test(it)
            }
        }
    }
}

fun <T : Any> FunSpec.tests(
    name: String,
    vararg cases: T,
    test: suspend TestContext.(T) -> Unit
) {
    context(name) {
        cases.forEach {
            test(it.testName) {
                test(it)
            }
        }
    }
}

suspend fun <T : Any> FunSpecContainerScope.test(
    name: String,
    cases: Iterable<T>,
    test: suspend TestContext.(T) -> Unit
) {
    context(name) {
        cases.forEach {
            test(it.testName) {
                test(it)
            }
        }
    }
}

suspend fun <T : Any> FunSpecContainerScope.tests(
    name: String,
    vararg cases: T,
    test: suspend TestContext.(T) -> Unit
) {
    context(name) {
        cases.forEach {
            test(it.testName) {
                test(it)
            }
        }
    }
}
