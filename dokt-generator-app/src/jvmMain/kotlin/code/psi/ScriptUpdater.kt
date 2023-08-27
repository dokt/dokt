package app.dokt.generator.code.psi

import org.jetbrains.kotlin.psi.KtFile

abstract class ScriptUpdater(func: () -> Unit, file: KtFile, env: Env) : FileUpdater(func, file, env) {
    protected val block = file.script!!.blockExpression
}
