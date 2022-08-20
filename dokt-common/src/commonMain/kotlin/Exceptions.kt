@file:Suppress("unused")

package app.dokt.common

@Suppress("NOTHING_TO_INLINE")
inline fun Any.throwIllegalState(message: String? = null): Nothing = throw IllegalStateException(message ?: toString())

data class Exceptions(val exceptions: List<Exception>, override val message: String? = null) : Exception(message)
