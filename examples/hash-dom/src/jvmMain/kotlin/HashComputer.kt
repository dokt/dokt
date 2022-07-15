package fi.papinkivi.hash

import java.security.MessageDigest
import java.util.*

actual class HashComputer actual constructor(private val algorithm: String) {
    actual fun hash(data: ByteArray): String = encoder.encodeToString(MessageDigest.getInstance(algorithm).digest(data))

    companion object {
        private val encoder = Base64.getEncoder()
    }
}