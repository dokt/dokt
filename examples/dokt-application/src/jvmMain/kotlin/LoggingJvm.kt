@file:Suppress("unused")

package app.dokt.app

fun logger(target: Any) = mu.KotlinLogging.logger(target.javaClass.name)
