package app.dokt.generator.domain.model

import app.dokt.generator.code.TypeDef
import app.dokt.generator.domain.*
import app.dokt.generator.pluralize

object BoundedContextModelReader : BoundedContextReader() {
    private fun List<*>.detected(name: String) {
        if (any()) log.info { "Detected ${name.pluralize(this)}." }
    }

    override fun readContext(rootDef: TypeDef, other: List<TypeDef>): BoundedContext {
        val exceptions = mutableListOf<DomainException>()
        val interfaces = mutableMapOf<String, TypeDef>()
        val valueObjects = mutableListOf<ValueObject>()
        other.forEach {
            when {
                it.isInterface -> interfaces[it.name] = it // TODO map using qualifiedName key
                it.isException -> exceptions.add(DomainExceptionModel(it))
                it.isData || it.isEnumeration || it.isValue -> valueObjects.add(ValueObjectModel(it))
                else -> log.warn { "$it type isn't detected!" }
            }
        }
        val eventsRef = (rootDef.extends ?: rootDef.rootInterface!!).argument
        val root = AggregateRootModel(rootDef, interfaces.remove(eventsRef.name)!!)
        val serviceInterfaces = interfaces.values.map { DomainServiceInterfaceModel(it) }
        valueObjects.detected("value object")
        exceptions.detected("exception")
        serviceInterfaces.detected("service interface")
        val context = BoundedContextModel(root, exceptions, root.module, serviceInterfaces, valueObjects)
        log.info {
            val detected = 2 + exceptions.size + interfaces.size + valueObjects.size
            "Detected $detected of ${"type".pluralize(other)} in $context bounded context."
        }
        return context
    }
}
