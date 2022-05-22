package app.dokt

data class Exceptions(val exceptions: List<Exception>, override val message: String? = null) : Exception(message)
