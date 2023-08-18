package app.dokt.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StringTest : FunSpec({
    context("camelCaseToWords") {
        mapOf(
            "word" to "word",
            "camelCase" to "camel Case",
            "PascalCase" to "Pascal Case",
            "CAPS" to "CAPS",
            "NameIDResolver" to "Name ID Resolver"
        ).forEach {
            test(it.toString()) { -> it.key.camelCaseToWords() shouldBe it.value }
        }
    }

    context("commonPrefix") {
        test("bar, baz") { listOf("bar", "baz").commonPrefix shouldBe "ba" }
        test("empty") { emptyList<String>().commonPrefix shouldBe "" }
        test("foo") { listOf("foo").commonPrefix shouldBe "foo" }
        test("foo, bar, baz") { listOf("foo", "bar", "baz").commonPrefix shouldBe "" }
    }

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
