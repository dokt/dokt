@file:Suppress("unused")

package app.dokt.infra

fun logger(target: Any) = mu.KotlinLogging.logger(target.javaClass.name)
