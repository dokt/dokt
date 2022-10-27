package app.dokt.infra

import kotlin.reflect.KProperty
import mu.*
import kotlin.properties.ReadWriteProperty

fun logger(func: () -> Unit) = KotlinLogging.logger(func)

/**
 * Examples
 * - "var bar: kotlin.String" Global property
 * - "var org.example.Foo.bar: kotlin.String" Class property
 */
val KProperty<*>.logger get() = KotlinLogging.logger(toString().substring(4).substringBefore(':'))

abstract class Logger(private val logger: KLogger) {
    constructor(func: () -> Unit) : this(KotlinLogging.logger(func))
    constructor(name: String) : this(KotlinLogging.logger(name))

    init {
        trace { "Initializing" }
    }

    protected fun trace(msg: () -> Any?) = logger.trace(msg)

    protected fun debug(msg: () -> Any?) = logger.debug(msg)

    protected fun info(msg: () -> Any?) = logger.info(msg)

    protected fun warn(msg: () -> Any?) = logger.warn(msg)

    protected fun warn(t: Throwable?, msg: () -> Any?) = logger.warn(t, msg)

    protected fun error(msg: () -> Any?) = logger.error(msg)

    protected fun error(t: Throwable?, msg: () -> Any?) = logger.error(t, msg)

    override fun toString(): String = logger.name
}

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
