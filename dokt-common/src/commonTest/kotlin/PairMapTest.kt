package app.dokt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class PairMapTest : FunSpec({
    val pair = Pair(1, 2)
    with(mapOf(pair to 3).toPairMap()) {
        context("get") {
            test("key1, key2") { get(1, 1) shouldBe null }

            test("pair") { get(Pair(1, 1)) shouldBe null }
        }

        context("getValue") {
            test("key1, key2") { getValue(1, 2) shouldBe 3 }

            test("pair") { getValue(pair) shouldBe 3 }
        }

        test("toPairMap") { values.first().shouldBeInstanceOf<Map<*, *>>() }
    }
})
