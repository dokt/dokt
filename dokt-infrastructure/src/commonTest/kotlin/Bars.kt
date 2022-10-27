package app.dokt.infra

val observers = mutableListOf<(String) -> Unit>()
var bar by Observable("bar", observers)
