/**
 * Domain model implementations with some logic.
 */
package app.dokt.generator.domain.model

import app.dokt.common.*
import app.dokt.generator.*
import app.dokt.generator.code.*
import app.dokt.generator.domain.*

class AggregateCommandModel(method: Method) : BuildingBlockModel<Method>(method), AggregateCommand {
    override val name by lazy { method.name.upperFirst }

    override val methodName get() = block.name

    override val values get() = block.parameters
}

class AggregateEventModel(override val method: Method) : BuildingBlockModel<Method>(method), AggregateEvent {
    override val name by lazy { method.name.upperFirst }

    override val details get() = method.parameters
}

class AggregateRootModel(root: TypeDef, private val eventsDef: TypeDef) : DomainObjectModel(root), AggregateRoot {
    override val commands by lazy {
        val eventsNames = events.map { it.method.name }
        methods.filter { it.name !in eventsNames }.map { AggregateCommandModel(it) }
    }

    override val events by lazy { eventsDef.methods.map { AggregateEventModel(it) } }

    override val eventsInterface by lazy { eventsDef.reference }

    override val id by lazy { block.primaryConstructor.firstOrNull() }
}

class BoundedContextModel(
    override val aggregateRoot: AggregateRootModel?,
    override val exceptions: List<DomainException>,
    module: String,
    override val serviceInterfaces: List<DomainServiceInterface>,
    override val valueObjects: List<ValueObject>,
) : BuildingBlockModel<ModuleModel>(ModuleModel(module)),
    BoundedContext {
    override val aggregateRoots = if (aggregateRoot == null) emptyList() else listOf(aggregateRoot)

    override val sharedKernel = name == "shared"

    override val displayName = if (sharedKernel) "Shared Kernel" else aggregateRoot?.displayName ?: name
}

abstract class BuildingBlockModel<B : Packaged>(protected val block: B) : BuildingBlock {
    override val blockType by lazy { this::class.simpleName!!.removeSuffix("Model").camelCaseToWords.lowercase() }

    override val displayName by lazy { name.camelCaseToWords }

    override val label by lazy { "$displayName $blockType" }

    override val module get() = block.packageName

    override val name get() = block.name

    override fun toString() = displayName
}

class DomainExceptionModel(definition: TypeDef): DomainObjectModel(definition), DomainException

abstract class DomainObjectModel(definition: TypeDef) : BuildingBlockModel<TypeDef>(definition), DomainObject {
    override val implements get() = block.implements

    override val label by lazy {
        "${super<BuildingBlockModel>.label}${if (implements.isEmpty()) "" else " extends ${implements.joinToString()}"}"
    }

    override val methods get() = block.methods

    override val properties get() = block.properties

    override val isCollection get() = implements.any { it.isCollection }
}

class DomainServiceInterfaceModel(definition: TypeDef) : DomainObjectModel(definition), DomainServiceInterface

data class ModuleModel(override val name: String, override val packageName: String) : Packaged {
    constructor(packageName: String) : this(
        packageName.substringAfterLast('.').upperFirst,
        packageName.substringBeforeLast('.')
    )
}

class ValueObjectModel(definition: TypeDef) : DomainObjectModel(definition), ValueObject
