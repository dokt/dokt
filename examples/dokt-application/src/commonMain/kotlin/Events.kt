@file:Suppress("UNCHECKED_CAST")

package app.dokt.app

import app.dokt.domain.event.*
import kotlinx.coroutines.*
import kotlin.reflect.KClass

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
