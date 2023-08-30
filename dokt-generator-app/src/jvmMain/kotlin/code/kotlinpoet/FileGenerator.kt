package app.dokt.generator.code.kotlinpoet

import app.dokt.infra.Logger
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import java.io.StringWriter
import kotlin.system.measureTimeMillis

/** Use [four spaces](https://kotlinlang.org/docs/coding-conventions.html#indentation) for indentation. */
private const val OFFICIAL_INDENT = "    "

open class FileGenerator(protected val file: FileSpec.Builder, func: () -> Unit) : Logger(func) {
    private val fileSpec by lazy {
        val fileSpec: FileSpec
        val ms = measureTimeMillis {
            build()
            fileSpec = file.build()
        }
        debug { "'${fileSpec.name}' was built in $ms ms." }
        fileSpec
    }

    protected val suppress = mutableSetOf<SuppressName>()

    val content by lazy {
        StringWriter().also {
            val ms = measureTimeMillis {
                fileSpec.writeTo(Sanitizer(it))
            }
            debug { "'${fileSpec.name}' was written and sanitized in $ms ms." }
        }.toString().trimEnd() // Removing new line from unit test, but it's kept on file.
    }

    init {
        file.indent(OFFICIAL_INDENT)
    }

    constructor(packageName: String, fileName: String) : this(FileSpec.builder(packageName, "$fileName.kt"), {})

    open fun build() {
        if (suppress.any()) file.addAnnotation(AnnotationSpec
            .builder(Suppress::class)
            .apply {
                suppress.map { it.warning }.sorted().forEach { addMember("%S", it) }
            }
            .build()
        )
    }
}
