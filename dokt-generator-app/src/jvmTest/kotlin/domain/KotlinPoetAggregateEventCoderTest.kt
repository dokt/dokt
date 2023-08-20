package app.dokt.generator.domain

import io.kotest.core.spec.style.FunSpec

class KotlinPoetAggregateEventCoderTest : FunSpec({
    test("data class") {
        landed.code() shouldBeSerializableInPlane """
                data class Landed(
                  val successful: Boolean,
                  val length: UShort,
                ) : PlaneEvent
                """
    }
    test("object") {
        takenOff.code() shouldBeSerializableInPlane "object TakenOff : PlaneEvent"
    }
    test("value class") {
        turned.code() shouldCodeInPlane """
                import kotlin.jvm.JvmInline
                import kotlinx.serialization.Serializable

                @JvmInline
                @Serializable
                value class Turned(
                  val degrees: Float,
                ) : PlaneEvent
                """
    }
}) {
    companion object {
        fun AggregateEvent.code() = KotlinPoetAggregateEventCoder(this, planeEvent).code()
    }
}
