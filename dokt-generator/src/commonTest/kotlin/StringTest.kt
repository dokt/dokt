package app.dokt.generator

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
})
