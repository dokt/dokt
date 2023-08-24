package app.dokt.infra

import io.github.oshai.kotlinlogging.*
import kotlin.reflect.KProperty
import kotlin.properties.ReadWriteProperty

fun logger(func: () -> Unit) = KotlinLogging.logger(func)

/**
 * Examples
 * - "var bar: kotlin.String" Global property
 * - "var org.example.Foo.bar: kotlin.String" Class property
 */
val KProperty<*>.logger get() = KotlinLogging.logger(toString().substring(4).substringBefore(':'))

interface Log {
    fun error(message: () -> Any?)

    fun error(throwable: Throwable?, message: () -> Any?)

    fun warn(message: () -> Any?)

    fun warn(throwable: Throwable?, message: () -> Any?)

    fun info(message: () -> Any?)

    fun debug(message: () -> Any?)

    fun trace(message: () -> Any?)
}

abstract class Logger(private val logger: KLogger) : Log {
    constructor(func: () -> Unit) : this(KotlinLogging.logger(func))
    constructor(name: String) : this(KotlinLogging.logger(name))

    init {
        trace { "Initializing" }
    }

    final override fun error(message: () -> Any?) = logger.error(message)

    final override fun error(throwable: Throwable?, message: () -> Any?) = logger.error(throwable, message)

    final override fun warn(message: () -> Any?) = logger.warn(message)

    final override fun warn(throwable: Throwable?, message: () -> Any?) = logger.warn(throwable, message)

    final override fun info(message: () -> Any?) = logger.info(message)

    final override fun debug(message: () -> Any?) = logger.debug(message)

    final override fun trace(message: () -> Any?) = logger.trace(message)

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
    private lateinit var logger: KLogger

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
