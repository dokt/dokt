package app.dokt.generator.code

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import io.kotest.matchers.shouldBe

class KotlinPoetTest : io.kotest.core.spec.style.FunSpec({
    test("default indent") {
        FileSpec.builder("", "")
            .addFunction(
                FunSpec.builder("main")
                .addStatement("print(%S)", "Hello").build())
            .build().toString() shouldBe """
                public fun main() {
                  print("Hello")
                }

            """.trimIndent()
    }
    test("indent 4 spaces") {
        FileSpec.builder("", "")
            .indent("    ")
            .addFunction(FunSpec.builder("main")
                .addStatement("print(%S)", "Hello").build())
            .build().toString() shouldBe """
                public fun main() {
                    print("Hello")
                }

            """.trimIndent()
    }
    test("control flow") {
        CodeBlock.builder().beginControlFlow("foo")
            .addStatement("val bar = %S", "baz")
            .endControlFlow()
            .build().toString() shouldBe """
                foo {
                  val bar = "baz"
                }

            """.trimIndent()
    }
})
