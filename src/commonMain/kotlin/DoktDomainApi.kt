/**
 * Dokt domain API package
 */
package app.dokt

import kotlinx.serialization.*

/**
 * Every aggregate root must implement this interface and be serializable to allow snapshotting.
 */
interface AggregateRoot<E : Any> {
    /**
     * Interface defining aggregate events as methods which are emitted and implemented in the aggregate root.
     */
    var emit: E
}

/**
 * Abstract base class of aggregate root.
 */
@Serializable
abstract class Root<E : Any> : AggregateRoot<E> {
    @Transient
    override lateinit var emit: E
}
