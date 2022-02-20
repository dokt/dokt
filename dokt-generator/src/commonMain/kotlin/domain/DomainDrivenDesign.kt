/**
 * Domain-driven design (DDD) API
 */
package app.dokt.generator.domain

import app.dokt.generator.*
import app.dokt.generator.code.*

val uuidRef = Ref("com.benasher44.uuid.Uuid")
val defaultIdVar = Var("id", uuidRef)

/**
 * https://en.wikipedia.org/wiki/Command_pattern
 */
interface AggregateCommand : BuildingBlock {
    val methodName get() = name.lowerFirst()

    val values: List<Variable>
}

/**
 * https://en.wikipedia.org/wiki/Event-driven_architecture
 */
interface AggregateEvent : BuildingBlock {
    /**
     * Event body
     */
    val details: List<Variable>

    val method: Method
}

interface AggregateRoot : DomainObject {
    val commands: List<AggregateCommand>

    val events: List<AggregateEvent>

    val eventsInterface: TypeRef

    /**
     * Aggregate ID that is passed to the constructor.
     * Null means that default UUID is used, and it isn't passed to the root.
     */
    val id: Variable? get() = null
}

interface BoundedContext : BuildingBlock {
    val aggregateRoot get() = aggregateRoots.firstOrNull()

    val aggregateRoots: List<AggregateRoot> get() = emptyList()

    val exceptions: List<DomainException>

    val serviceInterfaces: List<DomainServiceInterface>

    val valueObjects: List<ValueObject>

    val sharedKernel get() = name == "shared"

    override val displayName get() = if (sharedKernel) "Shared Kernel" else aggregateRoot?.displayName ?: name
}

interface BuildingBlock {
    val blockType get() = this::class.simpleName!!.camelCaseToWords().lowercase()

    val displayName get () = name.camelCaseToWords()

    val label get() = "$displayName $blockType"

    val module: String

    val name: String
}

/**
 * May be data class or singleton.
 */
interface DomainException: DomainObject

interface DomainObject : BuildingBlock {
    val implements: List<TypeRef>

    override val label get() =
        "${super.label}${if (implements.isEmpty()) "" else " extends ${implements.joinToString()}"}"

    val methods: List<Method>

    val properties: List<Variable>

    val isCollection get() = implements.any { it.isCollection }
}

interface DomainServiceInterface : DomainObject

/**
 * May be data class, enumeration, value object or object.
 */
interface ValueObject : DomainObject
