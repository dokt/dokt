package app.dokt.ui

import app.dokt.app.*

private val logger = mu.KotlinLogging.logger {}

abstract class ConsoleUI<A : Application> : UserInterface<A> {
    override var arguments = emptyArray<String>()

    override fun start() { handleArguments() }

    private fun handleArguments() {
        logger.info { "Starting with ${arguments.size} arguments." }
        arguments.forEachIndexed { index, argument ->
            logger.debug { "Handling argument $index: '$argument'." }
            if (handleArgument(index, argument)) logger.info { "Argument $index: '$argument' handled." }
            else logger.warn { "Argument $index: '$argument' not handled!" }
        }
    }

    open fun handleArgument(index: Int, argument: String) = false
}
