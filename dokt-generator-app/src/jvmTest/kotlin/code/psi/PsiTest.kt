package app.dokt.generator.code.psi

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PsiTest : FunSpec({
    test("kts") {
        Psi.parseKts("""print("Hello!")""").text shouldBe """print("Hello!")"""
    }
})
