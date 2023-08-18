package app.dokt.common

//#region [Scope functions](https://kotlinlang.org/docs/scope-functions.html#function-selection)
inline fun <T> T.alsoIf(expression: Boolean, block: T.() -> Unit): T {
    if (expression) block()
    return this
}

/** Executes given block (like `let` or `also`), but doesn't return anything. */
inline fun <T : Any> T.hit(block: (T) -> Unit) { block(this) }

/** Executes given block (like `run`, `with` or `apply`), but doesn't return anything. */
inline fun <T : Any> T.fly(block: T.() -> Unit) { block() }
//#endregion