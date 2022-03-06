package app.dokt.generator.domain

import app.dokt.generator.code.*
import app.dokt.generator.domain.data.*
import com.squareup.kotlinpoet.ClassName

const val PLANE = "com.airline.plane"
const val PLANE_APP = "com.airline.plane.app"

val byteType = "Byte".kotlinRef
val booleanType = "Boolean".kotlinRef
val uShortType = "UShort".kotlinRef
val floatType = "Float".kotlinRef

val gear = AggregateCommandData("gear", PLANE_APP)
val pilot = AggregateCommandData("pilot", PLANE_APP, Var("horizontal", byteType), Var("vertical", byteType))
val planeEvent = ClassName(PLANE, "PlaneEvent")
val takenOff = AggregateEventData("TakenOff", PLANE_APP)
val turned = AggregateEventData("Turned", PLANE_APP, Var("degrees", floatType))
val landed = AggregateEventData("Landed", PLANE_APP, Var("successful", booleanType), Var("length", uShortType))
val events = "$PLANE.Events".ref
val plane = AggregateRootData(
    "Plane",
    PLANE,
    listOf(gear, pilot),
    events,
    listOf(landed, takenOff, turned),
    Var("tailNo", stringRef)
)
