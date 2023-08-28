package app.dokt.generator.code.psi

open class ScriptEditor(context: Context, func: () -> Unit) : FileEditor(context, func) {
    protected val block = requireNotNull(file.script).blockExpression
}
