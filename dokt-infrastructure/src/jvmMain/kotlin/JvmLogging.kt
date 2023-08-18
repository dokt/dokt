@file:Suppress("unused")

package app.dokt.infra

import io.github.oshai.kotlinlogging.KotlinLogging

fun logger(target: Any) = KotlinLogging.logger(target.javaClass.name)
