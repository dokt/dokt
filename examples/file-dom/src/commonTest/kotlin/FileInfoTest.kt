package fi.papinkivi.file

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.serialization.json.Json

class FileInfoTest : FunSpec({
    FileInfo.files = mockk(relaxed = true)
    test("encodeToString") {
        Json.encodeToString(FileInfo.serializer(), FileInfo(FileId("empty.txt")).apply {
            emit = this
            create(byteArrayOf())
        }) shouldBe """{"id":"empty.txt","checksum":"1B2M2Y8AsgTpgAmY7PhCfg=="}"""
    }
})