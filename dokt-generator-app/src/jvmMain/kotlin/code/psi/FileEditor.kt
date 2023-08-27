package app.dokt.generator.code.psi

import app.dokt.infra.Logger

abstract class FileEditor(context: Context, func: () -> Unit) : Logger(func) {
    private val environment = context.environment

    val content by lazy {
        documentManager.doPostponedOperationsAndUnblockDocument(file.viewProvider.document!!)
        file.text!!
    }

    private val documentManager by lazy { environment.documentManager }

    private val factory by lazy { environment.factory }

    protected val file = context.file

    protected fun createExpression(text: String) = factory.createExpression(text)
}
