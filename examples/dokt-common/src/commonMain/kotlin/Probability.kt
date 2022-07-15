package app.dokt

@Suppress("unused")
data class Probability(val value: Double, private val event: String = "A") : Comparable<Probability> {
    companion object {
        const val ALWAYS = 1.0
        const val NEVER = 0.0
        val range = NEVER..ALWAYS
        val always = Probability(ALWAYS)
        val never = Probability(NEVER)
    }

    /**
     * The opposite or complement of an event
     */
    val complement get() = Probability(ALWAYS - value, "$event'")

    init {
        if (value !in range) throw IllegalArgumentException("$value isn't probability!")
    }


    override operator fun compareTo(other: Probability) = value.compareTo(other.value)

    override fun equals(other: Any?) = if (other is Probability) value == other.value else false

    override fun hashCode() = value.hashCode()

    /**
     * Probability of these mutually exclusive events (one or both) happens.
     * Mutually exclusive events doesn't have common cases.
     */
    operator fun plus(other: Probability) = Probability(value + other.value)

    override fun toString() = "P($event) = $value"
}
