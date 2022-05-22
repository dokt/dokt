package app.dokt.app

import kotlinx.coroutines.*
import kotlin.reflect.KClass

/**
 * Marker interface for all domain events
 */
interface DomainEvent

/**
 * Marker interface for events that are emitted from an aggregate root
 */
interface RootEvent: DomainEvent

/**
 * Marker interface for events that are emitted from a saga
 */
interface SagaEvent: DomainEvent

typealias Batch = UShort
typealias Version = UShort

/**
 * Domain event message which are emitted from aggregate root or saga. Contains metadata and the event itself.
 */
data class EventMessage<I, E : DomainEvent>(
    /**
     * User ID who caused creation of this event.
     */
    val user: UserId,

    /**
     * Source aggregate or saga ID. Source aggregate type can be detected from event interface.
     */
    val source: I,

    /**
     * Sequence number of the batch (committed transaction) in the aggregate or saga. The first batch number is zero.
     */
    val batch: Batch,

    /**
     * Sequence number of the event in the aggregate or saga. The first event number is one.
     */
    val version: Version,

    /**
     * Emitted domain event. It tells the unique event name and source aggregate root type or saga type via its sealed
     * interface.
     */
    val event: E
) {
    /**
     * A unique event name which is the same as the class simple name if not minified.
     */
    val eventName get() = event::class.simpleName!!

    /**
     * The instant when this message is created.
     */
    val sent = kotlinx.datetime.Clock.System.now()
}

abstract class EventHandler<I, E : DomainEvent>(val eventType: KClass<E>) {

    open suspend fun handle(event: E, message: EventMessage<I, out E>) {}

    open suspend fun handle(message: EventMessage<I, out E>) {
        handle(message.event, message)
    }
}

/**
 * Event bus
 */
interface EventBus {
    /**
     * Dispatch events emitted from single aggregate
     */
    suspend fun <I : Any, E : DomainEvent> dispatch(messages: List<EventMessage<I, out E>>)

    /**
     * Unregister event handler
     */
    operator fun <I, E : DomainEvent> minus(handler: EventHandler<I, E>): Boolean

    /**
     * Register event handler
     */
    operator fun <I, E : DomainEvent> plus(handler: EventHandler<I, E>): Boolean
}

/**
 * Store for all events
 */
interface EventStore {
    /**
     * Get all aggregate or saga event messages
     */
    suspend fun <I : Any, E : DomainEvent> get(id: I): List<EventMessage<I, E>>

    /**
     * Store at least one event emitted from single aggregate or saga
     */
    suspend fun <I : Any, E : DomainEvent> add(messages: List<EventMessage<I, out E>>)
}

object InMemEventStore : EventStore {
    private val eventsById = mutableMapOf<Any, MutableList<EventMessage<*, *>>>()

    override suspend fun <I : Any, E : DomainEvent> add(messages: List<EventMessage<I, out E>>) {
        eventsById.getOrPut(messages.first().source) { mutableListOf() }.addAll(messages)
    }

    fun clear() {
        eventsById.clear()
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <I : Any, E : DomainEvent> get(id: I) =
        eventsById[id] as List<EventMessage<I, E>>? ?: emptyList()
}

/**
 * Current event store implementation. Defaults to in-memory implementation.
 */
var store: EventStore = InMemEventStore

/**
 * Default event bus implementation which dispatches events only in local instance.
 */
class LocalEventBus(private val eventStore: EventStore = store) : EventBus {
    private val handlerRegistry = mutableMapOf<KClass<*>, MutableList<EventHandler<*, *>>>()

    override suspend fun <I : Any, E : DomainEvent> dispatch(messages: List<EventMessage<I, out E>>) {
        coroutineScope {
            launch {
                eventStore.add(messages)
            }
            messages.forEach { message ->
                getHandlers<I, E>(message.event::class).forEach {
                    launch {
                        it.handle(message)
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <I, E : DomainEvent> getHandlers(eventType: KClass<out E>) =
        handlerRegistry.getOrPut(eventType) { mutableListOf() } as MutableList<EventHandler<I, E>>

    override fun <I, E : DomainEvent> minus(handler: EventHandler<I, E>) =
        getHandlers<I, E>(handler.eventType).remove(handler)

    override fun <I, E : DomainEvent> plus(handler: EventHandler<I, E>) =
        getHandlers<I, E>(handler.eventType).add(handler)
}

/**
 * Current event bus implementation. Defaults to local implementation.
 */
var bus: EventBus = LocalEventBus(store)
