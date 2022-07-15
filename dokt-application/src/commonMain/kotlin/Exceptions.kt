@file:Suppress("unused")

package app.dokt.app

fun Any.missingService(type: String, name: String = "service", cause: Throwable? = null) =
    MissingService(cause, "Missing $type $name!", name, type)

fun Any.throwInvalid(value: Any, name: String = "value", cause: Throwable? = null) =
    throwIllegal(value, name, cause,"not valid!")

fun Any.throwUnsupported(value: Any, name: String = "value", cause: Throwable? = null) =
    throwIllegal(value, name, cause,"not supported!")

private fun Any.throwIllegal(value: Any, name: String, cause: Throwable?, reason: String) {
    throw IllegalArgument(name, value, value::class.simpleName, cause,
        "${this::class.simpleName} argument $name of type ${value::class.simpleName} value '$value' is $reason!",
        this, this::class.simpleName!!)
}

data class IllegalArgument(
    val argName: String,
    val argValue: Any,
    val argValueType: String?,
    override val cause: Throwable?,
    override val message: String,
    val valueObject: Any,
    val valueObjectType: String)
    : IllegalArgumentException(message, cause)

data class MissingService(
    override val cause: Throwable?,
    override val message: String,
    val serviceName: String,
    val serviceTypeName: String)
    : IllegalStateException(message, cause)
