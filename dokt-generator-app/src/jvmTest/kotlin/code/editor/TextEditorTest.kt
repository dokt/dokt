package app.dokt.generator.code.editor

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

val sample = """
The quick brown fox jumps over the lazy dog.
""".trim()

fun sut(act: TextEditor.() -> Unit) = TextEditor(sample).apply(act).toString()

@Suppress("EmptyRange", "SpellCheckingInspection")
class TextEditorTest : FunSpec({
    context("add") {
        test("negative") { sut { shouldThrow<IllegalArgumentException> { add(-1, "neg") } } }

        test("empty") { sut { add(0, "") } shouldBe sample }

        test("conflict") { sut {
            delete(19..<24) shouldBe " jump"
            shouldThrow<IllegalArgumentException> { add(20, "family ") }
        } }

        test("simple") { sut { add(10, "light ") } shouldBe
            "The quick light brown fox jumps over the lazy dog." }

        test("beginning twice") { sut {
            add(0, "Mac ")
            add(0, "great ")
        } shouldBe "Mac great The quick brown fox jumps over the lazy dog." }

        test("twice") { sut {
            add(10, "light ")
            add(16, "nice ")
        } shouldBe "The quick light brown nice fox jumps over the lazy dog." }
    }

    context("delete") {
        test("empty") { sut { delete(10..<10) shouldBe "" } shouldBe sample }

        test("almost conflict") { sut {
            add(20, "family ")
            delete(19..<24) shouldBe " jump"
        } shouldBe "The quick brown fox familys over the lazy dog." }

        test("add first") { sut {
            add(10, "light")
            delete(10..<15) shouldBe "brown"
        } shouldBe "The quick light fox jumps over the lazy dog." }

        test("add & delete twice") { sut {
            add(0, "A")
            delete(0..<3) shouldBe "The"
            delete(10..<16) shouldBe "brown "
        } shouldBe "A quick fox jumps over the lazy dog." }

        test("conflict") { sut {
            edit(4..<15, "fast red") shouldBe "quick brown"
            shouldThrow<IllegalArgumentException> { delete(10..<19) }
        } }

        test("simple") { sut {
            delete(10..<16) shouldBe "brown " } shouldBe "The quick fox jumps over the lazy dog." }

        test("character") { sut {
            delete(4..4) shouldBe "q" } shouldBe "The uick brown fox jumps over the lazy dog." }
    }

    context("edit") {
        test("empty") { sut {
            edit(10..<10, "fat ") shouldBe ""
        } shouldBe "The quick fat brown fox jumps over the lazy dog." }

        test("add") { sut { edit(10..<10, "light ") shouldBe "" } shouldBe
            "The quick light brown fox jumps over the lazy dog." }

        test("delete") { sut {
            edit(10..<16, "") shouldBe "brown " } shouldBe "The quick fox jumps over the lazy dog." }

        test("conflict") { sut {
            edit(4..<15, "fast red") shouldBe "quick brown"
            shouldThrow<IllegalArgumentException> { edit(4..<9, "slow") }
        } }

        test("simple") { sut {
            edit(4..<9, "slow") shouldBe "quick" } shouldBe "The slow brown fox jumps over the lazy dog." }

        test("character") { sut {
            edit(4..4, "b") shouldBe "q" } shouldBe "The buick brown fox jumps over the lazy dog." }
    }

    test("toString") { sut { } shouldBe sample }
})