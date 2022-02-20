package app.dokt.generator.application

import app.dokt.generator.documentation.Documentation
import app.dokt.generator.domain.*
import app.dokt.generator.pluralize

class ApplicationDocumentWriter(documentation: Documentation) : Documentation by documentation {
    fun documentApplication(application: Application) = with(application) {
        heading(application.name)
        application.description?.let { paragraph(it) }
        with(boundedContexts) {
            definition("Bounded contexts", "$size (${joinToString()})")
            horizontalRule()
            level { forEach(::documentBoundedContext) }
        }
    }

    private fun documentBoundedContext(boundedContext: BoundedContext) = with(boundedContext) {
        heading(label)
        definition("Module", module)
        aggregateRoot?.let { definition("Aggregate root", it) }
        definition("Other types", mapOf(
            "value object" to valueObjects,
            "exception" to exceptions,
            "service interface" to serviceInterfaces
        ).map { (type, list) -> type.pluralize(list) }.joinToString())
        level {
            aggregateRoot?.let {
                with(aggregateRoot) {
                    heading(label)
                    level {
                        this?.events?.forEach(::documentEvent)
                        exceptions.forEach(::documentException)
                        serviceInterfaces.forEach(::documentService)
                    }
                }
            }
            valueObjects.forEach(::documentValueObject)
        }
        horizontalRule()
    }

    private fun documentEvent(event: AggregateEvent) = with(event) {
        heading(label)
        ordered(details)
    }

    private fun documentException(exception: DomainException) = with(exception) {
        heading(label)
    }

    private fun documentService(service: DomainServiceInterface) = with(service) {
        heading(label)
    }

    private fun documentValueObject(valueObject: ValueObject) = with(valueObject) {
        heading(label)
        ordered(properties)
    }
}
