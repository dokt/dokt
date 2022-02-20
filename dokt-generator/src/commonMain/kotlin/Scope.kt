package app.dokt.generator

inline fun <T> T.alsoIf(expression: Boolean, block: T.() -> Unit): T {
    if (expression) block()
    return this
}
