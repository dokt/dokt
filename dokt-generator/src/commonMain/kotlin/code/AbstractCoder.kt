package app.dokt.generator.code

abstract class AbstractCoder<F, M>(
    val model: M,
    protected val generatedMain: GeneratedSources,
    protected val generatedTest: GeneratedSources
) : Coder {
    protected val log = mu.KotlinLogging.logger {}

    abstract fun F.toCodeFile() : CodeFile

    protected fun GeneratedSources.add(file: F) {
        val codeFile = file.toCodeFile()
        log.info { "Adding ${codeFile.name} to generated sources." }
        files.add(file.toCodeFile())
    }

    override fun toString() = "$model"
}
