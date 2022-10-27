package app.dokt.infra

import app.dokt.test.shouldBeTrue
import io.kotest.core.spec.style.FunSpec

class NetTest: FunSpec({
    test("privateIp") { privateIp?.privateIp4.shouldBeTrue() }
    test("publicIp") { publicIp?.publicIp4.shouldBeTrue() }
})
