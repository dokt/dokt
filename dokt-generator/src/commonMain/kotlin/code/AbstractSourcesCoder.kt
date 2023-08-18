package app.dokt.generator.code

import app.dokt.infra.Logger

abstract class AbstractSourcesCoder<F, M>(
    func: () -> Unit,
    val model: M,
    protected val generatedMain: GeneratedSources,
    protected val generatedTest: GeneratedSources
) : Logger(func), Coder {
    abstract fun F.toCodeFile() : CodeFile

    protected fun GeneratedSources.add(file: F) {
        val codeFile = file.toCodeFile()
        info { "Adding ${codeFile.name} to generated sources." }
        files.add(codeFile)
    }

    override fun toString() = "$model"
}