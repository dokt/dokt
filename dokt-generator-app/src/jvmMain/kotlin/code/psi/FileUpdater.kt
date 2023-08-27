package app.dokt.generator.code.psi

import app.dokt.infra.Logger
import org.jetbrains.kotlin.psi.KtFile

abstract class FileUpdater(func: () -> Unit, protected val file: KtFile, private val env: Env) : Logger(func) {
    private val documentManager by lazy { env.documentManager }

    private val factory by lazy { env.factory }

    val content by lazy {
        documentManager.doPostponedOperationsAndUnblockDocument(file.viewProvider.document!!)
        file.text!!
    }

    protected fun createExpression(text: String) = factory.createExpression(text)
}
