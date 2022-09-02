package fi.papinkivi.file

import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class FileInfoTest : FileInfoSpec({
    FileInfo.clock = object : Clock { override fun now() = Instant.DISTANT_PAST }
    FileInfo.files = mockk(relaxed = true)

    create {
        test("Corrupted") {
            fileInfo.act { create(byteArrayOf(), "") }.throws<Corrupted>()
        }
    }

    test("encodeToString") {
        Json.encodeToString(FileInfo.serializer(), FileInfo(testId).apply {
            emit = this
            create(byteArrayOf())
        }) shouldBe """{"id":"empty.txt","checksum":"1B2M2Y8AsgTpgAmY7PhCfg==","created":"-100001-12-31T23:59:59.999999999Z"}"""
    }
}, FileId("empty.txt"))