package app.dokt.infra

import java.io.IOException
import java.net.Inet4Address
import java.net.URL
import java.net.UnknownHostException
import java.util.*

private val logger = logger {}

actual val privateIp = try {
    Inet4Address.getLocalHost().hostAddress
} catch (e: UnknownHostException) {
    logger.error(e) { "Can't get private IP!" }
    null
}
actual val publicIp = try {
    Scanner(URL("https://api.ipify.org").openStream()).useDelimiter("\\A").use { it.next() }
} catch (e: IOException) {
    logger.error(e) { "Can't get public IP!" }
    null
}
