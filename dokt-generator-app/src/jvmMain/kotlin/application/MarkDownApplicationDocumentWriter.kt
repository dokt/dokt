package app.dokt.generator.application

import app.dokt.generator.documentation.Documentation
import app.dokt.generator.documentation.md.MarkDownDocumentation
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

class MarkDownApplicationDocumentWriter(private val application: Application) {
    fun document() = Path(application.generated).run {
        createDirectories()
        resolve("${application.appName}.md").toFile()
    }.bufferedWriter().use { document(MarkDownDocumentation(it)) }

    private fun document(documentation: Documentation) =
        ApplicationDocumentWriter(documentation).documentApplication(application)
}
