package app.dokt.generator.domain

import app.dokt.generator.kotlinpoet.shouldCode
import app.dokt.generator.kotlinpoet.shouldCodeSerializable
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

infix fun TypeSpec.shouldBeSerializableInPlane(code: String) = shouldCodeSerializable(code, PLANE)

infix fun TypeSpec.shouldCodeInPlane(code: String) = shouldCode(code, PLANE)

infix fun PropertySpec.shouldCodeInPlaneApp(code: String) = shouldCode(code, PLANE_APP)

infix fun TypeSpec.shouldCodeInPlaneApp(code: String) = shouldCode(code, PLANE_APP)
