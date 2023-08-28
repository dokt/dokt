package app.dokt.ui

import app.dokt.app.Application
import app.dokt.infra.Logger

abstract class ConsoleUI<A : Application>(func: () -> Unit) : Logger(func), UserInterface<A> {
    override var arguments = emptyArray<String>()

    override fun start() { handleArguments() }

    private fun handleArguments() {
        info { "Starting with ${arguments.size} arguments." }
        arguments.forEachIndexed { index, argument ->
            debug { "Handling argument $index: '$argument'." }
            if (handleArgument(index, argument)) info { "Argument $index: '$argument' handled." }
            else warn { "Argument $index: '$argument' not handled!" }
        }
    }

    open fun handleArgument(index: Int, argument: String) = false
}
