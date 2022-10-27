package app.dokt.infra

import app.dokt.common.*
import app.dokt.test.Headed
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse

@EnabledIf(Headed::class)
class GraphicsTest : FunSpec({
    test("centerPoint") { (centerPoint in CGA).shouldBeFalse() }
    test("headless") { headless.shouldBeFalse() }
})
