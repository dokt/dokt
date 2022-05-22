package app.dokt.app

val observers = mutableListOf<(String) -> Unit>()
var bar by Observable("bar", observers)
