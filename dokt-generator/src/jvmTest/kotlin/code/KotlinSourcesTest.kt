package app.dokt.generator.code

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.io.path.Path

class KotlinSourcesTest : FunSpec({
    context("commonRootPackage") {
        test("dokt") { dokt.commonRootPackage shouldBe "app.dokt.common" }
        test("erp") { erp.commonRootPackage shouldBe "com.corex.erp" }
        test("hello") { hello.commonRootPackage shouldBe "" }
        test("test") { test.commonRootPackage shouldBe "biz.bank.account" }
    }
    context("files") {
        test("dokt") { dokt.files shouldHaveSize 11 }
        test("erp") { erp.files shouldHaveSize 3 }
        test("hello") { hello.files shouldHaveSize 1 }
        test("test") { test.files shouldHaveSize 4 }
    }
    context("types") {
        test("dokt") { dokt.types shouldHaveSize 9 }
        test("erp") { erp.types shouldHaveSize 6 }
        test("hello") { hello.types shouldHaveSize 2 }
        test("test") { test.types shouldHaveSize 14 }
    }
}) {
    companion object {
        private const val MAIN = "src/commonMain"
        val dokt by lazy { KotlinSources("../dokt-common/$MAIN") }
        val erp by lazy { KotlinSources("../examples/erp-dom/$MAIN") }
        val hello by lazy { KotlinSources("../examples/hello-dom/$MAIN") }
        val test by lazy { KotlinSources("../dokt-domain-test/src/commonTest") }
    }
}
