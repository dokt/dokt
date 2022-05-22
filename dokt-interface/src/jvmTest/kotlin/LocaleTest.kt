package app.dokt.iface

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.*

class LocaleTest : FunSpec({
    context("getString") {
        test("en") {
            Locale.setDefault(en)
            bundle.getString(TEST) shouldBe TEST
        }
        test("fi") {
            Locale.setDefault(fi)
            bundle.getString(TEST) shouldBe "testi"
        }
    }
    context("nativeDisplayLanguage") {
        test("en") { en.nativeDisplayLanguage shouldBe "English"}
        test("fi") { fi.nativeDisplayLanguage shouldBe "suomi"}
    }
    context("nativeDisplayName") {
        test("en") { en.nativeDisplayName shouldBe "English"}
        test("fi") { fi.nativeDisplayName shouldBe "suomi (Suomi)"}
    }
}) {
    companion object {
        const val TEST = "test"

        val bundle: ResourceBundle get() = ResourceBundle.getBundle("LocaleTest")
    }
}
