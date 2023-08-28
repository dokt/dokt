package app.dokt.generator.kotlinpoet

import app.dokt.generator.code.KotlinPoetSanitizer
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import java.io.StringWriter

private const val SERIALIZABLE = "import kotlinx.serialization.Serializable\n\n@Serializable\n"

infix fun FileSpec.shouldCode(code: String) = shouldCode(code) { it }

fun FileSpec.shouldCode(
    code: String,
    filterPackage: Boolean = false,
    filter: (String) -> String = { it }
) {
    var generated = StringWriter().also { writer ->
        KotlinPoetSanitizer(writer).use { writeTo(it) }
    }.toString().trimEnd()
    if (filterPackage) {
        val packageDeclaration = "package $packageName\n\n"
        generated shouldStartWith packageDeclaration
        generated = generated.removePrefix(packageDeclaration)
    }
    generated = filter(generated)
    generated.trimStart() shouldBe code.trimIndent()
}

infix fun PropertySpec.shouldCode(code: String) = shouldCode(code, "")

fun PropertySpec.shouldCode(code: String, packageName: String) =
    FileSpec.builder(packageName, "PropertyTest").addProperty(this).build()
        .shouldCode(code, packageName.isNotBlank())

infix fun TypeSpec.shouldCode(code: String) = shouldCode(code, "")

fun TypeSpec.shouldCode(
    code: String,
    packageName: String,
    filter: (String) -> String = { it }
) = FileSpec.get(packageName, this).shouldCode(code, packageName.isNotBlank(), filter)

fun TypeSpec.shouldCodeSerializable(code: String, packageName: String) = shouldCode(code, packageName) {
    it shouldStartWith SERIALIZABLE
    it.removePrefix(SERIALIZABLE)
}
