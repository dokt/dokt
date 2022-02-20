package app.dokt.test

import app.dokt.AggregateRoot
import app.dokt.app.*
import io.kotest.assertions.withClue
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

interface EventCollector {
    val collectedEvents: MutableList<*>
}

abstract class RootSerializer<R>(val root: R, private val serializer: KSerializer<R>) {
    protected fun serialize() = Json.encodeToString(serializer, root)
}

abstract class TestAggregate<Root: AggregateRoot<Events>, Events : Any, Event : RootEvent>(root: Root, serializer: KSerializer<Root>)
    : RootSerializer<Root>(root, serializer), EventCollector {

    var commands = 0
        private set

    protected val command: Root get() {
        ++commands
        return root
    }

    @Suppress("UNCHECKED_CAST")
    private val emit get() = this as Events

    private val events = mutableListOf<Event>()

    override val collectedEvents = events

    private var state = serialize()

    init { root.emit = emit }

    /**
     * Applying event and ensuring that it changes the aggregate root's state.
     * TODO Apply time should be less than 10 ms.
     */
    protected fun apply(event: Event, apply: Root.() -> Unit) {
        apply(root)
        val newState = serialize()
        withClue("Applying event $event doesn't changed state!") { newState shouldNotBe state }
        state = newState
        events.add(event)
    }
}

class Arranger<Commands, Root, Events, Event>(private val aggregate: EventCollector) {
    @Suppress("UNCHECKED_CAST")
    operator fun invoke(apply: (Events.() -> Unit)? = null) = Actor<Commands, Root, Event>(aggregate.apply {
        apply?.let {
            it(aggregate as Events)
            withClue("No events arranged!") { collectedEvents shouldNotHaveSize 0 }
            collectedEvents.clear()
        }
    } as Commands)
}

class Actor<Commands, Root, Event>(private val aggregate: Commands) {
    fun <Result> act(commander: Commands.() -> Result) = Asserter<Commands, Root, Event, Result>(aggregate, commander)
}

class Asserter<Commands, Root, Event, Result>(private val aggregate: Commands, commander: Commands.() -> Result) {
    var exception: Exception? = null
    private var result: Result? = null

    init {
        try {
            result = commander(aggregate)
        } catch (exception: Exception) {
            this.exception = exception
        }
    }

    /**
     * Doesn't emit anything.
     */
    val nothing: Asserter<Commands, Root, Event, Result> get() {
        withClue("Should not emit events!") {
            (aggregate as EventCollector).collectedEvents shouldHaveSize 0
        }
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun assert(assertions: Root.() -> Unit) {
        withClue("Aggregate root state doesn't match!") {
            assertions((aggregate as RootSerializer<Root>).root)
        }
    }

    fun emits(vararg events: Event): Asserter<Commands, Root, Event, Result> {
        withClue("Emitted events doesn't match!") {
            (aggregate as EventCollector).collectedEvents shouldContainInOrder events.toList()
        }
        return this
    }

    fun returns(expected: Result): Asserter<Commands, Root, Event, Result> {
        withClue("Command thrown unexpected exception!") { exception shouldBe null }
        withClue("Unexpected command result!") { result shouldBe expected }
        return this
    }

    inline fun <reified E : Exception> throws() {
        withClue("Command doesn't throw exception!") { exception shouldNotBe null }
        withClue("Command thrown wrong exception type!") { exception!!::class shouldBe E::class }
    }
}
