package app.dokt.generator.application

import app.dokt.app.AggregateRepository
import app.dokt.app.EventStoreRepository
import app.dokt.generator.code.GeneratedSources
import app.dokt.generator.code.KotlinPoetFile
import app.dokt.generator.code.overrideBuilder
import app.dokt.generator.code.write
import app.dokt.generator.domain.KotlinPoetBoundedContextCoder
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName

class KotlinPoetApplicationCoder(application: Application)
    : ApplicationCoder<FileSpec, TypeSpec>({}, application) {
    companion object {
        private val aggregateRepositoryType = AggregateRepository::class.asTypeName()
        private val applicationType = app.dokt.app.Application::class.asTypeName()
        // TODO Define domain service interface?
        private val domainServiceType = ClassName("app.dokt", "DomainService")
        private val eventStoreRepositoryType = EventStoreRepository::class.asTypeName()
    }

    override val boundedContextCoders =
        model.boundedContexts.map { KotlinPoetBoundedContextCoder(it, generatedMain, generatedTest) }

    private val domainServices: List<ClassName> = emptyList()

    override fun codeTestInitializer(): TypeSpec {
        val initDomainServices = FunSpec.overrideBuilder("initDomainServices")
            .addKdoc("Override this test method to initialize and register domain service with real implementations.")
        domainServices.forEach {
            // TODO Domain service registration
            initDomainServices.addStatement("%T.register(%T)", domainServiceType, it)
        }
        val initRepositories = FunSpec.overrideBuilder("initRepositories")
            .addKdoc("Initializes and registers aggregate repositories.")
        boundedContextCoders.flatMap { it.aggregateCoders }.forEach {
            // TODO Repository registration
            initRepositories.addStatement("%T.register(%T(eventStore))",
                aggregateRepositoryType,
                eventStoreRepositoryType.parameterizedBy(it.rootType, it.idType, it.eventType))
        }
        return TypeSpec.classBuilder(appName + "TestApplication")
            .addModifiers(KModifier.OPEN)
            .superclass(applicationType)
            .addFunction(initDomainServices.build())
            .addFunction(FunSpec.overrideBuilder("initEventHandlers")
                .addKdoc("Override this test method to initialize event handlers and add them to event bus.")
                .build())
            .addFunction(initRepositories.build())
            .build()
    }

    override fun GeneratedSources.write() {
        files.forEach { (it as KotlinPoetFile).spec.write(this) }
    }

    override fun FileSpec.toCodeFile() = KotlinPoetFile(this)
}
