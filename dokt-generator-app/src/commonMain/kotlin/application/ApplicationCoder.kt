package app.dokt.generator.application

import app.dokt.common.upperFirst
import app.dokt.generator.code.AbstractSourcesCoder
import app.dokt.generator.code.GeneratedSources
import app.dokt.generator.domain.BoundedContextCoder

abstract class ApplicationCoder<F, T>(func: () -> Unit, application: Application): AbstractSourcesCoder<F, Application>(
    func,
    application,
    GeneratedSources(application.generatedSources),
    GeneratedSources(application.generatedTestSources)
) {
    protected abstract val boundedContextCoders: List<BoundedContextCoder<*, *>>

    protected val appName = application.appName.upperFirst

    override fun  code() {
        boundedContextCoders.forEach {
            info { "Coding $it bounded context" }
            it.code()
        }

        info { "Writing ${generatedMain.files.size} files to ${generatedMain.basePath}" }
        generatedMain.write()

        info { "Writing ${generatedTest.files.size} files to ${generatedTest.basePath}" }
        generatedTest.write()

        info { "Finished application coding." }
    }

    abstract fun codeTestInitializer() : T

    protected abstract fun GeneratedSources.write()
}
