package app.dokt.infra.ktor

import io.ktor.server.routing.HttpMethodRouteSelector
import io.ktor.server.routing.Route

private const val ROUTE_INDENT = 7

/** https://stackoverflow.com/questions/69739723/how-to-list-configured-routes-in-ktor */
val Route.endpoints get() = routes.filter { it.selector is HttpMethodRouteSelector }

val Route.method get() = (selector as? HttpMethodRouteSelector)?.method
val Route.methodText get() = method?.value ?: ""
val Route.path get() = toString().substringBeforeLast('/').ifBlank { "/" }

/** List all routes (this and children). */
val Route.routes get(): List<Route> = listOf(this) + children.flatMap { it.routes }

val Route.text get() = "${methodText.padStart(ROUTE_INDENT)} $path"
