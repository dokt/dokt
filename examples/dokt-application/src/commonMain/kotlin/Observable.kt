package app.dokt.app

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Observable property
 * https://kotlinlang.org/docs/delegated-properties.html
 */
class Observable<V>(
    initialValue: V,
    private val observers: List<(V) -> Unit>,
    private val equalizer: (V, V) -> Boolean = { old, new -> old == new }
) : ReadWriteProperty<Any?, V> {
    private lateinit var logger: mu.KLogger

    private var value = initialValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        if (!::logger.isInitialized) logger = property.logger
        logger.trace { "<- $value" }
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        if (!::logger.isInitialized) logger = property.logger
        val old = this.value
        if (equalizer(old, value)) logger.debug { "-> $value"}
        else {
            this.value = value
            logger.info { "${observers.size}: $old -> $value" }
            observers.forEach {
                try {
                    it(value)
                } catch (e: Exception) {
                    logger.error(e) { "Observer #${it.hashCode()} $it failed!" }
                }
            }
        }
    }
}
