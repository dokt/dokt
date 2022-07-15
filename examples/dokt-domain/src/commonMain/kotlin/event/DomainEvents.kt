@file:Suppress("unused")

/**
 * Dokt Domain API infrastructure package
 */
package app.dokt.domain.event

import kotlin.reflect.KClass
import kotlinx.datetime.Clock

/**
 * Marker interface for all domain events
 */
interface DomainEvent

/**
 * Marker interface for events that are emitted from an aggregate root. These are generated based on events interface.
 */
interface RootEvent: DomainEvent

/**
 * Marker interface for events that are emitted from a saga
 */
interface SagaEvent: DomainEvent

/**
 * User ID which is e.g. case-insensitive unique token.
 */
interface UserId

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
    val sent = Clock.System.now()
}

abstract class EventHandler<I, E : DomainEvent>(val eventType: KClass<E>) {

    open suspend fun handle(event: E, message: EventMessage<I, out E>) {}

    open suspend fun handle(message: EventMessage<I, out E>) {
        handle(message.event, message)
    }
}
