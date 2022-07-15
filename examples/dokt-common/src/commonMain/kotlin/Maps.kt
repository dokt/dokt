package app.dokt

typealias PairMap<F, S, V> = Map<F, Map<S, V>>

@Suppress("unused")
fun <K, V> Map<K, V>.firstValueByKey(defaultValue: V, keyPredicate: (K) -> Boolean) =
    keys.firstOrNull(keyPredicate)?.let { getValue(it) } ?: defaultValue

/**
 * Create a pair map which is two-dimensional map.
 */
fun <F, S, V> Map<Pair<F, S>, V>.toPairMap(): PairMap<F, S, V> {
    val byFirst = mutableMapOf<F, MutableMap<S, V>>()
    forEach { (pair, value) ->
        val first = pair.first
        val bySecond = byFirst[first] ?: mutableMapOf()
        if (bySecond.isEmpty()) byFirst[first] = bySecond
        bySecond[pair.second] = value
    }
    return byFirst
}

operator fun <F, S, V> PairMap<F, S, V>.get(first: F, second: S) = get(first)?.get(second)

operator fun <F, S, V> PairMap<F, S, V>.get(pair: Pair<F, S>) = get(pair.first, pair.second)

fun <F, S, V> PairMap<F, S, V>.getValue(first: F, second: S) = getValue(first).getValue(second)

fun <F, S, V> PairMap<F, S, V>.getValue(pair: Pair<F, S>) = getValue(pair.first, pair.second)
