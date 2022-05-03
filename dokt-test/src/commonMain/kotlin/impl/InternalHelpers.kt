package app.dokt.test.impl

val Any?.testName get() = when (this) {
    null -> "null"
    is Char -> "'$this'"
    is String -> "\"$this\""
    else -> "$this"
}

data class Case2<A : Any?, B: Any?>(val a: A, val b: B) {
    private val name = "${a.testName} = ${b.testName}"

    override fun toString() = name
}

data class Case3<A : Any?, B: Any?, C: Any?>(val a: A, val b: B, val c: C) {
    private val name = "${a.testName}, ${b.testName} = ${c.testName}"

    override fun toString() = name
}
