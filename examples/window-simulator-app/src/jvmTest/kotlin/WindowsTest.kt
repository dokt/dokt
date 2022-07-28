package fi.papinkivi.simulator

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe

class WindowsTest : FunSpec({
    test("all") {
        Windows.all shouldNotBe emptyList<WindowInfo>()
    }
})
