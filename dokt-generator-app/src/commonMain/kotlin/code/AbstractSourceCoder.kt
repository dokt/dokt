package app.dokt.generator.code

import app.dokt.infra.Logger

/**
 * @param F file model implementation
 */
abstract class AbstractSourceCoder<F>(
    func: () -> Unit,
    private val generated: GeneratedSources
) : Logger(func), Coder {
    override fun code() {
        codeFiles().forEach {
            val codeFile = it.toCodeFile()
            info { "Adding ${codeFile.name} to generated sources." }
            generated.files.add(codeFile)
        }

        info { "Writing ${generated.files.size} files to ${generated.basePath}" }
        generated.write()

        info { "Finished coding." }
    }

    protected abstract fun codeFiles(): List<F>

    abstract fun F.toCodeFile() : CodeFile

    protected abstract fun GeneratedSources.write()
}
