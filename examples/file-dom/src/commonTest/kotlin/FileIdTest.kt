package fi.papinkivi.file

impost app.dokt.test.jsonShouldBe
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

class FileIdTest : FunSpec({
    test("serialize") {
        val name = "test.txt"
        FileId(name) jsonShouldBe "\"$name\""
    }
})