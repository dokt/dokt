package app.dokt.common

import app.dokt.test.test
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NetTest : FunSpec({
    context("Ip") {
        val ips = listOf(
            "0.0.0.0" to 0,
            GOOGLE_SECONDARY_DNS to 134743044,
            "127.0.0.1" to 2130706433,
            "192.168.1.1" to -1062731519,
            "255.255.255.255" to -1,
        )
        test("String.ip", ips) { it.first.ip.no shouldBe  it.second }
        test("toString", ips) { Ip(it.second).toString() shouldBe it.first }
    }
})