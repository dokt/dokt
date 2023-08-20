package app.dokt.generator.domain.model

import app.dokt.generator.code.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize

class BoundedContextModelReaderTest : FunSpec({
    context("readContexts") {
        test("dokt") { dokt shouldHaveSize 0 }
        test("erp") { erp shouldHaveSize 1 }
        test("hello") { hello shouldHaveSize 1 }
        test("test") { test shouldHaveSize 1 }
    }
}) {
    companion object {
        val dokt by KotlinSourcesTest.dokt.contexts
        val erp by KotlinSourcesTest.erp.contexts
        val hello by KotlinSourcesTest.hello.contexts
        val test by KotlinSourcesTest.test.contexts

        private val Sources.contexts get() = lazy { BoundedContextModelReader.readContexts(this) }
    }
}
