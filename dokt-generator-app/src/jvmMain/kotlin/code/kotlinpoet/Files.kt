package app.dokt.generator.code.kotlinpoet

import app.dokt.generator.code.controlFlow
import app.dokt.infra.logger
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import java.io.StringWriter
import kotlin.system.measureTimeMillis

/** Use [four spaces](https://kotlinlang.org/docs/coding-conventions.html#indentation) for indentation. */
private const val OFFICIAL_INDENT = "    "

private val logger = logger { }

val FileSpec.snippet get() = StringWriter().also {
    val ms = measureTimeMillis { writeTo(Sanitizer(it)) }
    logger.debug { "Wrote and sanitized snippet in $ms ms." }
}.toString()

val FileSpec.Builder.snippet get() = build().snippet

fun script() = FileSpec.scriptBuilder("").indent(OFFICIAL_INDENT)

fun script(build: FileSpec.Builder.() -> Unit) = script().apply(build)

fun FileSpec.Builder.addControlFlow(controlFlow: String, vararg args: Any?, code: CodeBlock.Builder.() -> Unit) =
    addCode(controlFlow(controlFlow, *args) { code() })
