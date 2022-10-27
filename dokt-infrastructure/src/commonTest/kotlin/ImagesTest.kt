package app.dokt.infra

import app.dokt.common.origin
import app.dokt.test.Headed
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

@EnabledIf(Headed::class)
class ImagesTest : FunSpec({
    context("Capture") {
        test("pixel") { Capture(origin).shouldNotBeNull() }
        test("screen") {
            val screenshot = Capture()
            screenshot shouldBe null
        }
    }
})