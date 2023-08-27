package app.dokt.generator.code.psi

abstract class ScriptEditor(context: Context, func: () -> Unit) : FileEditor(context, func) {
    protected val block = file.script!!.blockExpression
}
