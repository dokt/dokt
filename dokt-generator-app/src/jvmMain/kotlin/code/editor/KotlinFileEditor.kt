package app.dokt.generator.code.editor

import app.dokt.generator.code.psi.Psi
import app.dokt.infra.Logger
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

open class KotlinFileEditor(
    text: String,
    fileName: String,
    val func: () -> Unit = {}
) : Logger(func) {
    val file = Psi.parse(fileName, text)

    protected val editor = TextEditor(text)

    val content get() = editor.toString()

    protected fun PsiElement.replace(replacement: String) =
        editor.replace(startOffset..<endOffset, replacement).let {
            require(text == it) { "PSI element:\n${this.text}\nand replaced text:\n$it\ndoesn't match!" }
        }
}
