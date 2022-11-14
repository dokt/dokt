package app.dokt.infra.ktor

import app.dokt.infra.hostname
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*

/** HTML hyperlink target attribute which opens the linked document in a new window or tab. */
const val BLANK = "_blank"

suspend inline fun ApplicationCall.respondBody(titlePrefix: String, crossinline body : BODY.() -> Unit) {
    respondHtml {
        head {
            title {
                +"$titlePrefix - $hostname"
            }
            meta("color-scheme", "dark")
        }
        body {
            body()
        }
    }
}

fun CommonAttributeGroupFacade.color(code: String) { style = "color:$code" }

@HtmlTagMarker
inline fun DL.definition(term: String, crossinline definition: DD.() -> Unit) {
    dt { +term }
    dd(block = definition)
}

@HtmlTagMarker
inline fun FlowContent.group(legend: String, crossinline block : FIELDSET.() -> Unit) {
    fieldSet {
        legend { +legend }
        block()
    }
}

fun Routing.endpointsRoute(path: String = "index") {
    get(path) {
        call.respondBody("Endpoints") {
            table {
                caption { +"Endpoints" }
                thead {
                    tr {
                        th { +"Method" }
                        th { +"Path" }
                    }
                }
                tbody {
                    this@endpointsRoute.endpoints.sortedBy { it.toString() }.forEach {
                        tr {
                            td {
                                style = "text-align:right"
                                +it.methodText
                            }
                            td {
                                if (it.method == HttpMethod.Get) a(it.path, BLANK) { +it.path }
                                else +it.path
                            }
                        }
                    }
                }
            }
        }
    }
}