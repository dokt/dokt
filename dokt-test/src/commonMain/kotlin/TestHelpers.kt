@file:Suppress("unused")

package app.dokt.test

import app.dokt.test.impl.Case2
import app.dokt.test.impl.Case3
import app.dokt.test.impl.testName
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.core.test.TestContext
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.floats.shouldBeGreaterThan
import io.kotest.matchers.floats.shouldBeLessThan
import io.kotest.matchers.floats.shouldBeWithinPercentageOf

private const val FULL = 100f
private const val HALF_PERCENT = 0.005f
private const val HALF_PERCENTAGE = 0.5

fun <A : Any?, B: Any?> c(a: A, b: B) = Case2(a, b)

fun <A : Any?, B: Any?, C: Any?> c(a: A, b: B, c: C) = Case3(a, b, c)

infix fun Float.shouldBePercent(expected: Int) {
    val percentage = expected / FULL
    val min = percentage - HALF_PERCENT
    val max = percentage + HALF_PERCENT
    withClue("expected:<$min..$max> but was:<$this>") {
        shouldBeGreaterThan(min)
        shouldBeLessThan(max)
    }
}

infix fun Float.shouldBeRoughly(expected: Double) = shouldBeRoughly(expected.toFloat())

infix fun Float.shouldBeRoughly(expected: Float) = shouldBeWithinPercentageOf(expected, HALF_PERCENTAGE)

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

suspend fun <T : Any, R> FunSpecContainerScope.testsWithMatcher(
    name: String,
    matcher: R.() -> Unit,
    vararg cases: T,
    test: suspend TestContext.(T) -> R
) {
    context(name) {
        cases.forEach {
            test(it.testName) {
                test(it).matcher()
            }
        }
    }
}
