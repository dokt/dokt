@file:Suppress("unused", "MemberVisibilityCanBePrivate", "CanBeParameter")

package app.dokt.app

import app.dokt.domain.event.*
import kotlin.reflect.KClass

/**
 * Command target (named "To" because "Target is reserved in JVM")
 */
data class To<I>(
    /**
     * Commander user ID
     */
    val user: UserId,

    /**
     * Command target (aggregate or saga) ID
     */
    val id: I,

    /**
     * Version which command target aggregate root must match if exists.
     */
    val version: UShort? = null
) {
    val mayExists get() = !new

    /**
     * Target (aggregate or saga) must be new
     */
    val new get() = version == UShort.MIN_VALUE
}

/**
 * Concurrent modification thrown when target version isn't same as expected.
 */
class VersionMismatch(val id: Any, val actual: Version, val expected: Version) : Exception(
    "Expecting to modify $expected version of $id, but the version was $actual!"
)

class DomainException(val id: Any, cause: Exception) : Exception(cause)

abstract class ApplicationService<R : Any, I : Any, E : RootEvent>(rootType: KClass<R>) {
    private val repository = AggregateRepository.get<R, I, E>(rootType)

    protected suspend fun <T> tx(to: To<I>, command: R.() -> T) = with(repository.get(to.id)) {
        val result: T
        val events: List<EventMessage<I, E>>
        mutex.lock()
        try {
            to.version?.let { if (version != it) throw VersionMismatch(id, version, it) }
            user = to.user
            result = root.command()
            events = commit()
        } catch (exception: VersionMismatch) {
            throw exception
        } catch (exception: Exception) {
            rollback()
            throw DomainException(id, exception)
        } finally {
            mutex.unlock()
        }
        bus.dispatch(events)
        result
    }
}
