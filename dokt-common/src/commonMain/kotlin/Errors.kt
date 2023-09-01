@file:Suppress("NOTHING_TO_INLINE", "unused")

package app.dokt.common

data class Exceptions(val exceptions: List<Exception>, override val message: String? = null) : Exception(message)

/** Throws illegal state exception with message and throwable in line. */
inline fun error(message: String, cause: Throwable): Nothing =
    throw IllegalStateException(message, cause)

/** Throws illegal argument exception with message and throwable in line. */
inline fun require(message: String, cause: Throwable): Nothing =
    throw IllegalArgumentException(message, cause)

/** Throws illegal argument exception with message and throwable in line. */
inline fun <T> requireNull(value: T?, lazyMessage: (T) -> String = { "Required null value was '$it'!" }) =
    require(value == null) { lazyMessage(value!!) }

/** Throws unsupported operation exception with message in line. */
inline fun unsupported(message: String, cause: Throwable? = null): Nothing =
    throw UnsupportedOperationException(message, cause)
