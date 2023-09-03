package app.dokt.generator.code.psi

import app.dokt.infra.Logger

open class FileReader(context: Context, func: () -> Unit) : Logger(func) {
    private val environment = context.environment

    val content by lazy {
        documentManager.doPostponedOperationsAndUnblockDocument(file.viewProvider.document!!)
        file.text!!
    }

    protected val documentManager by lazy { environment.documentManager }

    protected val factory by lazy { environment.factory }

    protected val file = context.file

    protected fun createExpression(text: String) = factory.createExpression(text)
}
