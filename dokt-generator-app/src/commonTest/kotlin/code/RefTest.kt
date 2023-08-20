package app.dokt.generator.code

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe

class RefTest : FunSpec({
    test("argument") {
        entry.argument shouldBe string
    }

    test("arguments") {
        entry.arguments shouldContainInOrder listOf(string, Ref("kotlin.Any"))
    }

    test("name") {
        entry.name shouldBe "Map.Entry"
    }

    test("packageName") {
        entry.packageName shouldBe "kotlin.collections"
    }

    test("simpleNames") {
        entry.simpleNames shouldContainInOrder listOf("Map", "Entry")
    }

    test("toString") {
        string.toString() shouldBe "kotlin.String"
    }
}) {
    companion object {
        val string = Ref(String::class)
        val entry = Ref("kotlin.collections.Map.Entry<$string, kotlin.Any>")
    }
}
