package app.dokt.generator.code.psi

open class ScriptReader(context: Context, func: () -> Unit) : FileReader(context, func) {
    protected val block = requireNotNull(file.script).blockExpression
}
