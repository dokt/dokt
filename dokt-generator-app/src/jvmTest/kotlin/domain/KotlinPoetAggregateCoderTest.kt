package app.dokt.generator.domain

import app.dokt.generator.kotlinpoet.shouldCode
import io.kotest.core.spec.style.FunSpec

class KotlinPoetAggregateCoderTest : FunSpec({
    val coder = KotlinPoetAggregateCoder(plane)

    test("codeAggregate") {
        coder.codeAggregate() shouldCodeInPlaneApp """
            import app.dokt.app.Aggregate
            import com.airline.plane.Events
            import com.airline.plane.Plane
            import com.airline.plane.PlaneEvent

            class PlaneAggregate(
              tailNo: String,
            ) : Aggregate<Plane, String, PlaneEvent>(tailNo), Events {
              override fun PlaneEvent.apply() = when(this) {
                is Landed -> root.landed(successful, length)
                is TakenOff -> root.takenOff()
                is Turned -> root.turned(degrees)
              }

              override fun create() = Plane(id).also { it.emit = this }

              override fun landed(successful: Boolean, length: UShort) {
                root.landed(successful, length)
                add(Landed(successful, length))
              }

              override fun takenOff() {
                root.takenOff()
                add(TakenOff)
              }

              override fun turned(degrees: Float) {
                root.turned(degrees)
                add(Turned(degrees))
              }
            }
            """
    }

    test("codeApplication") {
        coder.codeApplication() shouldCode """
            @file:Suppress("unused")

            package com.airline.plane.app

            import app.dokt.app.Aggregate
            import app.dokt.app.ApplicationService
            import app.dokt.app.To
            import com.airline.plane.Events
            import com.airline.plane.Plane
            import com.airline.plane.PlaneEvent

            class PlaneAggregate(
              tailNo: String,
            ) : Aggregate<Plane, String, PlaneEvent>(tailNo), Events {
              override fun PlaneEvent.apply() = when(this) {
                is Landed -> root.landed(successful, length)
                is TakenOff -> root.takenOff()
                is Turned -> root.turned(degrees)
              }

              override fun create() = Plane(id).also { it.emit = this }

              override fun landed(successful: Boolean, length: UShort) {
                root.landed(successful, length)
                add(Landed(successful, length))
              }

              override fun takenOff() {
                root.takenOff()
                add(TakenOff)
              }

              override fun turned(degrees: Float) {
                root.turned(degrees)
                add(Turned(degrees))
              }
            }

            object PlaneService : ApplicationService<Plane, String, PlaneEvent>(Plane::class) {
              suspend fun gear(to: To<String>) = tx(to) { gear() }

              suspend fun pilot(
                to: To<String>,
                horizontal: Byte,
                vertical: Byte,
              ) = tx(to) { pilot(horizontal, vertical) }
            }
            """
    }

    test("codeApplicationTest") {
        coder.codeApplicationTest() shouldCode """
            package com.airline.plane.app

            import app.dokt.domain.test.TestAggregate
            import com.airline.plane.Events
            import com.airline.plane.Plane
            import com.airline.plane.PlaneEvent
            import kotlinx.serialization.KSerializer

            private val serializer: KSerializer<Plane> = Plane.serializer()

            interface PlaneCommands {
              fun gear()

              fun pilot(horizontal: Byte, vertical: Byte)
            }

            class PlaneTestAggregate(
              root: Plane,
            ) : TestAggregate<Plane, Events, PlaneEvent>(root, serializer), PlaneCommands, Events {
              override fun gear() = command.gear()

              override fun pilot(horizontal: Byte, vertical: Byte) = command.pilot(horizontal, vertical)

              override fun landed(successful: Boolean, length: UShort) = apply(Landed(successful, length))
                  { landed(successful, length) }

              override fun takenOff() = apply(TakenOff) { takenOff() }

              override fun turned(degrees: Float) = apply(Turned(degrees)) { turned(degrees) }
            }
            """
    }

    test("codeCommands") {
        coder.codeCommands() shouldCodeInPlaneApp """
            interface PlaneCommands {
              fun gear()

              fun pilot(horizontal: Byte, vertical: Byte)
            }
            """
    }

    test("codeDomain") {
        coder.codeDomain() shouldCode """
            package com.airline.plane

            import app.dokt.domain.event.RootEvent
            import kotlin.jvm.JvmInline
            import kotlinx.serialization.Serializable

            sealed interface PlaneEvent : RootEvent

            @Serializable
            data class Landed(
              val successful: Boolean,
              val length: UShort,
            ) : PlaneEvent

            @Serializable
            object TakenOff : PlaneEvent

            @JvmInline
            @Serializable
            value class Turned(
              val degrees: Float,
            ) : PlaneEvent
            """
    }

    test("codeDomainTest") {
        coder.codeDomainTest() shouldCode """
            package com.airline.plane

            import app.dokt.domain.test.Actor
            import app.dokt.domain.test.Arranger
            import com.airline.plane.app.PlaneCommands
            import com.airline.plane.app.PlaneTestAggregate
            import io.kotest.core.spec.style.FunSpec
            import io.kotest.core.spec.style.scopes.FunSpecContainerScope

            @Suppress("unused", "MemberVisibilityCanBePrivate")
            abstract class PlaneSpec(
              body: PlaneSpec.() -> Unit,
              val testTailNo: String = "testTailNo",
            ) : FunSpec() {
              init {
                body()}

              val plane: Actor<PlaneCommands, Plane, PlaneEvent> =
                  Actor(PlaneTestAggregate(Plane(testTailNo)))

              fun plane(tailNo: String = testTailNo, apply: (Events.() -> Unit)? = null) =
                  Arranger<PlaneCommands, Plane, Events, PlaneEvent>(PlaneTestAggregate(Plane(tailNo)))(apply)

              fun gear(test: suspend FunSpecContainerScope.() -> Unit) = context("gear", test)

              fun pilot(test: suspend FunSpecContainerScope.() -> Unit) = context("pilot", test)
            }
            """
    }

    test("codeEvent") {
        coder.codeEvent() shouldCodeInPlaneApp """
            import app.dokt.domain.event.RootEvent

            sealed interface PlaneEvent : RootEvent
            """
    }

    test("codeSerializer") {
        coder.codeSerializer() shouldCodeInPlaneApp """
            import com.airline.plane.Plane
            import kotlinx.serialization.KSerializer

            private val serializer: KSerializer<Plane> = Plane.serializer()
            """
    }

    test("codeService") {
        coder.codeService() shouldCodeInPlaneApp """
            import app.dokt.app.ApplicationService
            import app.dokt.app.To
            import com.airline.plane.Plane
            import com.airline.plane.PlaneEvent

            object PlaneService : ApplicationService<Plane, String, PlaneEvent>(Plane::class) {
              suspend fun gear(to: To<String>) = tx(to) { gear() }

              suspend fun pilot(
                to: To<String>,
                horizontal: Byte,
                vertical: Byte,
              ) = tx(to) { pilot(horizontal, vertical) }
            }
            """
    }

    test("codeSpec") {
        coder.codeSpec() shouldCodeInPlane """
            import app.dokt.domain.test.Actor
            import app.dokt.domain.test.Arranger
            import com.airline.plane.app.PlaneCommands
            import com.airline.plane.app.PlaneTestAggregate
            import io.kotest.core.spec.style.FunSpec
            import io.kotest.core.spec.style.scopes.FunSpecContainerScope

            @Suppress("unused", "MemberVisibilityCanBePrivate")
            abstract class PlaneSpec(
              body: PlaneSpec.() -> Unit,
              val testTailNo: String = "testTailNo",
            ) : FunSpec() {
              init {
                body()}

              val plane: Actor<PlaneCommands, Plane, PlaneEvent> =
                  Actor(PlaneTestAggregate(Plane(testTailNo)))

              fun plane(tailNo: String = testTailNo, apply: (Events.() -> Unit)? = null) =
                  Arranger<PlaneCommands, Plane, Events, PlaneEvent>(PlaneTestAggregate(Plane(tailNo)))(apply)

              fun gear(test: suspend FunSpecContainerScope.() -> Unit) = context("gear", test)

              fun pilot(test: suspend FunSpecContainerScope.() -> Unit) = context("pilot", test)
            }
            """
    }

    test("codeTestAggregate") {
        coder.codeTestAggregate() shouldCodeInPlaneApp """
            import app.dokt.domain.test.TestAggregate
            import com.airline.plane.Events
            import com.airline.plane.Plane
            import com.airline.plane.PlaneEvent

            class PlaneTestAggregate(
              root: Plane,
            ) : TestAggregate<Plane, Events, PlaneEvent>(root, serializer), PlaneCommands, Events {
              override fun gear() = command.gear()

              override fun pilot(horizontal: Byte, vertical: Byte) = command.pilot(horizontal, vertical)

              override fun landed(successful: Boolean, length: UShort) = apply(Landed(successful, length))
                  { landed(successful, length) }

              override fun takenOff() = apply(TakenOff) { takenOff() }

              override fun turned(degrees: Float) = apply(Turned(degrees)) { turned(degrees) }
            }
            """
    }
})
