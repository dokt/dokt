package app.dokt.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.matchers.collections.*
import io.kotest.matchers.comparables.*
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

suspend fun FunSpecContainerScope.testV(no: String, vTest: Version.() -> Unit) = test(no) { Version(no).vTest() }
suspend fun FunSpecContainerScope.testV(less: String, greater: String) = test("$less < $greater") {
    Version(less) shouldBeLessThan Version(greater)
}

class VersionTest: FunSpec({
    context("core") {
        testV("1.2.3") { core shouldBe "1.2.3" }
        testV("1.2") { core shouldBe "1.2" }
        testV("1") { core shouldBe "1" }
        testV("1.2.3-4") { core shouldBe "1.2.3" }
        testV("1.2-4") { core shouldBe "1.2" }
        testV("1-4") { core shouldBe "1" }
        testV("1.2.3+4") { core shouldBe "1.2.3" }
        testV("1.2+4") { core shouldBe "1.2" }
        testV("1+4") { core shouldBe "1" }
    }
    context("compareTo") {
        testV("1.0.0-alpha", "1.0.0")
        testV("1.0.0-alpha", "1.0.0-alpha.1")
        testV("1.0.0-alpha.1", "1.0.0-alpha.beta")
        testV("1.0.0-alpha.beta", "1.0.0-beta")
        testV("1.0.0-beta", "1.0.0-beta.2")
        testV("1.0.0-beta.2", "1.0.0-beta.11")
        testV("1.0.0-beta.11", "1.0.0-rc.1")
        testV("1.0.0-rc.1", "1.0.0")
        testV("1.0.0-alpha", "1.0.0")
        testV("1.0.0", "2.0.0")
        testV("2.0.0", "2.1.0")
        testV("2.1.0", "2.1.1")
        test("1.0.0 = 1.0.0+1") { Version("1.0.0") shouldBeEqualComparingTo Version("1.0.0+1") }
        test("1.0.0+1 = 1.0.0+2") { Version("1.0.0+1") shouldBeEqualComparingTo Version("1.0.0+2") }
    }
    context("build") {
        testV("1.2.3") { build.shouldBeNull() }
        testV("1.2") { build.shouldBeNull() }
        testV("1") { build.shouldBeNull() }
        testV("1.2.3-4") { build.shouldBeNull() }
        testV("1.2-4") { build.shouldBeNull() }
        testV("1-4") { build.shouldBeNull() }
        testV("1.2.3+4") { build shouldBe "4" }
        testV("1.2+4") { build shouldBe "4" }
        testV("1+4") { build shouldBe "4" }
    }
    context("major") {
        testV("1.2.3") { major shouldBe 1 }
        testV("1.2") { major shouldBe 1 }
        testV("1") { major shouldBe 1 }
        testV("1.2.3-4") { major shouldBe 1 }
        testV("1.2-4") { major shouldBe 1 }
        testV("1-4") { major shouldBe 1 }
        testV("1.2.3+4") { major shouldBe 1 }
        testV("1.2+4") { major shouldBe 1 }
        testV("1+4") { major shouldBe 1 }
    }
    context("minor") {
        testV("1.2.3") { minor shouldBe 2 }
        testV("1.2") { minor shouldBe 2 }
        testV("1") { minor shouldBe 0 }
        testV("1.2.3-4") { minor shouldBe 2 }
        testV("1.2-4") { minor shouldBe 2 }
        testV("1-4") { minor shouldBe 0 }
        testV("1.2.3+4") { minor shouldBe 2 }
        testV("1.2+4") { minor shouldBe 2 }
        testV("1+4") { minor shouldBe 0 }
    }
    context("patch") {
        testV("1.2.3") { patch shouldBe 3 }
        testV("1.2") { patch shouldBe 0 }
        testV("1") { patch shouldBe 0 }
        testV("1.2.3-4") { patch shouldBe 3 }
        testV("1.2-4") { patch shouldBe 0 }
        testV("1-4") { patch shouldBe 0 }
        testV("1.2.3+4") { patch shouldBe 3 }
        testV("1.2+4") { patch shouldBe 0 }
        testV("1+4") { patch shouldBe 0 }
    }
    context("preRelease") {
        testV("1.2.3") { preRelease.shouldBeNull() }
        testV("1.2") { preRelease.shouldBeNull() }
        testV("1") { preRelease.shouldBeNull() }
        testV("1.2.3-4") { preRelease shouldBe "4" }
        testV("1.2-4") { preRelease shouldBe "4" }
        testV("1-4") { preRelease shouldBe "4" }
        testV("1.2.3+4") { preRelease.shouldBeNull() }
        testV("1.2+4") { preRelease.shouldBeNull() }
        testV("1+4") { preRelease.shouldBeNull() }
    }
    context("preReleaseIds") {
        testV("1.2.3") { preReleaseIds.shouldBeEmpty() }
        testV("1.2") { preReleaseIds.shouldBeEmpty() }
        testV("1") { preReleaseIds.shouldBeEmpty() }
        testV("1.2.3-4") { preReleaseIds.shouldContainExactly("4") }
        testV("1.2-4") { preReleaseIds.shouldContainExactly("4") }
        testV("1-4") { preReleaseIds.shouldContainExactly("4") }
        testV("1.2.3+4") { preReleaseIds.shouldBeEmpty() }
        testV("1.2+4") { preReleaseIds.shouldBeEmpty() }
        testV("1+4") { preReleaseIds.shouldBeEmpty() }
    }
})