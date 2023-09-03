package app.dokt.generator.code.kotlinpoet

import com.squareup.kotlinpoet.AnnotationSpec

/** Compiler warning */
enum class Warning(private val value: String) {
    SpellCheckingInspection("SpellCheckingInspection"),
    Unused("unused");

    override fun toString() = value
}

fun suppress(vararg names: Warning) = suppress(names.toSet())

fun suppress(names: Set<Warning>) = AnnotationSpec.builder(Suppress::class)
    .apply { names.map { it.toString() }.sorted().forEach { addMember("%S", it) } }.build()
