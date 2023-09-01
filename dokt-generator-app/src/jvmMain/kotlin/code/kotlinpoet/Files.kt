package app.dokt.generator.code.kotlinpoet

import app.dokt.generator.code.controlFlow
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec

fun FileSpec.Builder.addControlFlow(controlFlow: String, vararg args: Any?, code: CodeBlock.Builder.() -> Unit) =
    addCode(controlFlow(controlFlow, *args) { code() })
