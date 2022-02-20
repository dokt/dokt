package app.dokt.generator.code

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.io.path.Path

class KotlinSourcesTest : FunSpec({
    context("commonRootPackage") {
        test("dokt") { dokt.commonRootPackage shouldBe "app.dokt" }
        test("erp") { erp.commonRootPackage shouldBe "com.corex.erp" }
        test("hello") { hello.commonRootPackage shouldBe "" }
        test("test") { test.commonRootPackage shouldBe "biz.bank" }
    }
    context("files") {
        test("dokt") { dokt.files shouldHaveSize 6 }
        test("erp") { erp.files shouldHaveSize 3 }
        test("hello") { hello.files shouldHaveSize 1 }
        test("test") { test.files shouldHaveSize 9 }
    }
    context("types") {
        test("dokt") { dokt.types shouldHaveSize 22 }
        test("erp") { erp.types shouldHaveSize 6 }
        test("hello") { hello.types shouldHaveSize 2 }
        test("test") { test.types shouldHaveSize 18 }
    }
}) {
    companion object {
        private const val MAIN = "src/commonMain/kotlin"
        val dokt by lazy { KotlinSources(Path("../$MAIN")) }
        val erp by lazy { KotlinSources(Path("../examples/erp/$MAIN")) }
        val hello by lazy { KotlinSources(Path("../examples/hello/$MAIN")) }
        val test by lazy { KotlinSources(Path("../dokt-test/src/commonTest/kotlin")) }
    }
}
