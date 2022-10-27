package app.dokt.infra

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldNotBeBlank

class SystemTest: FunSpec({
    test("username") { username.shouldNotBeBlank() }
    test("userDir") { userDir.shouldNotBeBlank() }
    test("hostname") { hostname.shouldNotBeBlank() }
    test("osArch") { osArch.shouldNotBeBlank() }
    test("osName") { osName.shouldNotBeBlank() }
    test("osVer") { osVer.shouldNotBeBlank() }
})
