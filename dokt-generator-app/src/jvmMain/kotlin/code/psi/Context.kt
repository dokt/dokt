package app.dokt.generator.code.psi

import org.jetbrains.kotlin.psi.KtFile

data class Context(val environment: Environment, val file: KtFile)
