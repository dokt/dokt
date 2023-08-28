package app.dokt.infra

private const val IP_MAX_OCTET = 255
private const val IP_4_OCTETS = 4

private val ipRange = 0..IP_MAX_OCTET
private val privateIpPrefixes = listOf("10.", "172.16.", "192.168.")

expect val privateIp: String?
expect val publicIp: String?

val String.ip4 get() = with(split(".")) {
    size == IP_4_OCTETS && all { str -> str.toIntOrNull()?.let { it in ipRange } ?: false }
}
val String.privateIp4 get() = ip4 && privateIpPrefixes.any { startsWith(it) }
val String.publicIp4 get() = !privateIp4
