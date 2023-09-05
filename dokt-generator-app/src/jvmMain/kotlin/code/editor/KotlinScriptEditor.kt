package app.dokt.generator.code.editor

import app.dokt.generator.code.controlFlow
import app.dokt.generator.code.kotlinpoet.addControlFlow
import app.dokt.generator.code.kotlinpoet.script
import app.dokt.generator.code.kotlinpoet.snippet
import app.dokt.generator.code.psi.binaries
import app.dokt.generator.code.psi.callStringValue
import app.dokt.generator.code.psi.findBlock
import app.dokt.generator.code.psi.findCall
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import org.jetbrains.kotlin.psi.KtBlockExpression

open class KotlinScriptEditor(text: String, fileName: String, func: () -> Unit = {}) :
    KotlinFileEditor(text, fileName, func) {
    protected val block = requireNotNull(file.script).blockExpression

    protected fun applyPlugins(vararg plugins: Pair<String, String>) = prependCall("plugins") { block ->
        plugins.toMap().let { required ->
            block.binaries.associate { it.callStringValue to it.text }.toMutableMap().apply {
                if (required.map { putIfAbsent(it.key, it.value) }.all { it != null } ) return@let false
            }.values.forEach { addStatement(it) }
            true
        }
    }

    protected fun append(action: FileSpec.Builder.() -> Boolean) = edit(true, action)

    private fun edit(append: Boolean, action: FileSpec.Builder.() -> Boolean): Boolean {
        var edited = false
        script {
            edited = action()
        }.apply {
            if (edited) {
                if (append) editor.append(snippet) else editor.prepend(snippet)
            }
        }
        return edited
    }

    protected fun appendCall(to: String, action: CodeBlock.Builder.(KtBlockExpression?) -> Boolean) =
        editCall(to, true, action)

    protected fun prependCall(to: String, action: CodeBlock.Builder.(KtBlockExpression?) -> Boolean) =
        editCall(to, false, action)

    private fun editCall(to: String, append: Boolean, action: CodeBlock.Builder.(KtBlockExpression?) -> Boolean):
        Boolean {
        val call = block.findCall(to)
        var edited = false
        script {
            addControlFlow(to) {
                edited = action(call.findBlock())
            }
        }.apply {
            if (edited) {
                if (call == null) { if (append) editor.append(snippet) else editor.prepend(snippet) }
                else call.replace(snippet)
            }
        }
        return edited
    }

    protected fun CodeBlock.Builder.call(
        from: KtBlockExpression?,
        to: String,
        action: CodeBlock.Builder.(KtBlockExpression?) -> Unit) = controlFlow(to) {
            action(from.findCall(to).findBlock())
        }
}
