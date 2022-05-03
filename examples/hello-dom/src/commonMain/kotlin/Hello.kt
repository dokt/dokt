import app.dokt.Root
import kotlinx.serialization.Serializable

/** Aggregate events as methods that the Greeter may emit */
interface Events {
    fun greeted(greeting: String)
}

/** Greeter aggregate root. Identified by UUID (default). */
@Serializable // For unit testing
class Greeter : Root<Events>(), Events {
    private val greetings = mutableListOf<String>()

    /** Greet command handler which has the domain logic. */
    fun greet(who: String) {
        if (who.isBlank()) throw IllegalArgumentException("Missing 'who'!")
        emit.greeted("Hello, $who!")
    }

    /** Greeted event handler applies the event. */
    override fun greeted(greeting: String) {
        greetings.add(greeting)
    }
}
