/**
 * Domain data implementations with some logic.
 */
package app.dokt.generator.domain.data

import app.dokt.common.*
import app.dokt.generator.*
import app.dokt.generator.code.*
import app.dokt.generator.code.data.MethodData
import app.dokt.generator.domain.*
import kotlinx.serialization.Serializable

@Serializable
data class AggregateCommandData(
    override val name: String,
    override val values: List<Variable> = emptyList(),
    override val module: String = ""
) : AggregateCommand {
    override val methodName get() = name.lowerFirst

    constructor(method: Method, module: String = "") :
            this(method.name.upperFirst, method.parameters, module)

    constructor(name: String, module: String = "", vararg values: Variable) :
            this(name, values.toList(), module)
}

@Serializable
data class AggregateEventData(
    override val name: String,
    override val details: List<Variable> = emptyList(),
    override val module: String = ""
) : AggregateEvent {
    constructor(method: Method, module: String = "") :
            this(method.name.upperFirst, method.parameters, module)

    constructor(name: String, module: String = "", vararg details: Variable) :
            this(name, details.toList(), module)

    override val method get() = MethodData(name.lowerFirst, details)
}

@Serializable
data class AggregateRootData(
    override val name: String,
    override val module: String = "",
    override val commands: List<AggregateCommand> = emptyList(),
    override val eventsInterface: TypeRef,
    override val events: List<AggregateEvent> = emptyList(),
    override val id: Variable? = null,
    override val implements: List<TypeRef> = emptyList(),
    override val methods: List<Method> = emptyList(),
    override val properties: List<Variable> = emptyList(),
) : AggregateRoot

@Serializable
data class BoundedContextData(
    override val aggregateRoot: AggregateRoot?,
    override val exceptions: List<DomainException>,
    override val serviceInterfaces: List<DomainServiceInterface>,
    override val valueObjects: List<ValueObject>,
    override val module: String = "",
    override val name: String
) : BoundedContext

@Serializable
data class DomainExceptionData(
    override val module: String = "",
    override val name: String,
    override val implements: List<TypeRef>,
    override val methods: List<Method>,
    override val properties: List<Variable>
) : DomainException

@Serializable
data class DomainServiceInterfaceData(
    override val module: String = "",
    override val name: String,
    override val implements: List<TypeRef>,
    override val methods: List<Method>,
    override val properties: List<Variable>
) : DomainServiceInterface

@Serializable
data class ValueObjectData(
    override val module: String = "",
    override val name: String,
    override val implements: List<TypeRef>,
    override val methods: List<Method>,
    override val properties: List<Variable>
) : ValueObject
