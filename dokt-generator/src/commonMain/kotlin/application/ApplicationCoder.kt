package app.dokt.generator.application

import app.dokt.generator.code.*
import app.dokt.generator.domain.*
import app.dokt.generator.upperFirst

abstract class ApplicationCoder<F, T>(application: Application): AbstractCoder<F, Application>(
    application,
    GeneratedSources(application.generatedSources),
    GeneratedSources(application.generatedTestSources)
) {
    protected abstract val boundedContextCoders: List<BoundedContextCoder<*, *>>

    protected val name = application.appName.upperFirst()

    override fun code() {
        boundedContextCoders.forEach {
            log.info { "Coding $it bounded context" }
            it.code()
        }

        log.info { "Writing ${generatedMain.files.size} files to ${generatedMain.basePath}" }
        generatedMain.write()

        log.info { "Writing ${generatedTest.files.size} files to ${generatedTest.basePath}" }
        generatedTest.write()

        log.info { "Finished application coding." }
    }

    abstract fun codeTestInitializer() : T

    protected abstract fun GeneratedSources.write()
}
