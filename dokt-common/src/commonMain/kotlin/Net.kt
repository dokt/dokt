package app.dokt.common

import kotlinx.serialization.Serializable

//#region IP v4 address
/** [Google Public DNS](https://developers.google.com/speed/public-dns) */
const val GOOGLE_SECONDARY_DNS = "8.8.4.4"

val String.ip get() = Ip(ipNo)
val String.ipNo get() = split('.').map { it.toInt() }.ipNo
val List<Int>.ipNo get() = reduce{ value, octet -> value shl 8 or octet }

@OptIn(ExperimentalUnsignedTypes::class)
val Int.ipAddress get() = UByteArray(4) { shr(it * 8).toUByte() }.reversed().joinToString(".")

/** IP v4 address */
@JvmInline
@Serializable
value class Ip(val no: Int) {
    constructor(address: String) : this(address.ipNo)
    constructor(d: Int, c: Int, b: Int, a: Int = 10) : this(listOf(a, b, c, d).ipNo)

    override fun toString() = no.ipAddress
}
//#endregion