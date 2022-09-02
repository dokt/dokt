package fi.papinkivi.file

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

class FileIdTest : FunSpec({
    test("serialize") {
        val name = "test.txt"
        Json.encodeToString(FileId.serializer(), FileId(name)) shouldBe "\"$name\""
    }
})