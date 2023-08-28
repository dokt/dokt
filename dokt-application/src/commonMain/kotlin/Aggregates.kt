@file:Suppress("unused")

package app.dokt.app

import app.dokt.domain.event.EventMessage
import app.dokt.domain.event.RootEvent
import app.dokt.domain.event.UserId
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

abstract class Aggregate<R : Any, I, E : RootEvent>(val id: I) {
    /**
     * Current transaction order number in this aggregate.
     */
    private var batch = UShort.MIN_VALUE

    /**
     * Mutually exclusive lock for this aggregate ID.
     */
    val mutex = Mutex()

    val root by lazy { create() }

    private val transaction = mutableListOf<EventMessage<I, E>>()

    /**
     * Last emitted event sequence number.
     */
    var version = UShort.MIN_VALUE
        private set

    /**
     * Commander user that creates events in current transaction.
     */
    lateinit var user: UserId

    /**
     * Add event to current transaction.
     */
    protected fun add(event: E) {
        transaction.add(EventMessage(user, id, batch, ++version, event))
    }

    abstract fun E.apply()

    fun apply(events: List<EventMessage<I, E>>) = with(events) {
        lastOrNull()?.let { last ->
            batch = last.batch
            batch++
            version = last.version
            forEach { it.event.apply() }
        }
        this@Aggregate
    }

    /**
     * Create the initial aggregate root.
     */
    protected abstract fun create(): R

    fun commit() : List<EventMessage<I, E>> {
        val events = transaction.toList()
        transaction.clear()
        ++batch
        return events
    }

    fun rollback() {
        version = (version - transaction.size.toUShort()).toUShort()
        transaction.clear()
    }
}

typealias AggregateFactory<R, I, E> = (I) -> Aggregate<R, I, E>

abstract class AggregateRepository<R : Any, I : Any, E : RootEvent>(
    protected val factory: AggregateFactory<R, I, E>
) {
    companion object {
        val registry = mutableMapOf<KClass<*>, AggregateRepository<*, *, *>>()

        inline operator fun <reified R : Any> plus(instance: AggregateRepository<R, *, *>) {
            registry[R::class] = instance
        }

        @Suppress("UNCHECKED_CAST")
        fun <R : Any, I : Any, E : RootEvent> get(type: KClass<R>) = registry[type] as AggregateRepository<R, I, E>?
            ?: throw missingService("AggregateRepository<${type.simpleName}>", type.simpleName!!)
    }

    abstract suspend fun get(id: I): Aggregate<R, I, E>
}

open class CachingAggregateRepository<R : Any, I : Any, E : RootEvent>(
    factory: AggregateFactory<R, I, E>
) : AggregateRepository<R, I, E>(factory) {
    private val cache = mutableMapOf<I, Aggregate<R, I, E>>()
    private val mutex = Mutex()

    override suspend fun get(id: I) = mutex.withLock { cache.getOrPut(id) { factory(id) } }
}

class EventStoreRepository<R : Any, I : Any, E : RootEvent>(
    factory: AggregateFactory<R, I, E>,
    private val eventStore: EventStore = store,
) : AggregateRepository<R, I, E>(factory) {
    override suspend fun get(id: I) = factory(id).apply(eventStore.get(id))
}
