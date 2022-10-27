package app.dokt.infra

import mu.KotlinLogging
import java.net.*
import java.util.*

private val logger = KotlinLogging.logger {}

actual val privateIp = try {
    Inet4Address.getLocalHost().hostAddress
} catch (e: Exception) {
    logger.error(e) { "Can't get private IP!" }
    null
}
actual val publicIp = try {
    Scanner(URL("https://api.ipify.org").openStream()).useDelimiter("\\A").use { it.next() }
} catch (e: Exception) {
    logger.error(e) { "Can't get public IP!" }
    null
}
