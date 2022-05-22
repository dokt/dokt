package app.dokt.app

import kotlin.reflect.KProperty
import mu.*

/**
 * Examples
 * - "var bar: kotlin.String" Global property
 * - "var org.example.Foo.bar: kotlin.String" Class property
 */
val KProperty<*>.logger get() = KotlinLogging.logger(toString().substring(4).substringBefore(':'))
