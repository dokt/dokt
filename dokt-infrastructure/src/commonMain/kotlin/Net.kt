package app.dokt.infra

private val ipRange = 0..255
private val privateIpPrefixes = listOf("10.", "172.16.", "192.168.")

expect val privateIp: String?
expect val publicIp: String?

val String.ip4 get() = with(split(".")) {
    size == 4 && all { str -> str.toIntOrNull()?.let { it in ipRange } ?: false }
}
val String.privateIp4 get() = ip4 && privateIpPrefixes.any { startsWith(it) }
val String.publicIp4 get() = !privateIp4