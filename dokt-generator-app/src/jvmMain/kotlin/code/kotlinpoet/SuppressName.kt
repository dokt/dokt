package app.dokt.generator.code.kotlinpoet

enum class SuppressName(
    /** Name of the compilation warning. */
    val warning: String
) {
    SpellCheckingInspection("SpellCheckingInspection"),
    Unused("unused")
}
