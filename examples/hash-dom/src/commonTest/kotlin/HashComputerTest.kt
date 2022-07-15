package fi.papinkivi.hash

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class HashComputerTest : FunSpec({
    context("hash") {
        val str = "foo"
        val bytes = str.toByteArray()
        mapOf(
            Hash.MD5 to "rL0Y20zC+Fzt72VPzMSk2A==",
            Hash.SHA_1 to "C+7Hteo/D9vJXQ3UfzxbwnXaijM=",
            Hash.SHA_256 to "LCa0a2j/xo/5m0U8HTBBNBNCLXBkg7+g+YpeiGJm564="
        ).forEach { (algorithm, hash) ->
            context(algorithm.id) {
                test("ByteArray") { algorithm.hash(bytes) shouldBe hash }
                test("String") { algorithm.hash(str) shouldBe hash }
            }
        }
    }
    context("id") {
        test(MD) { Hash.MD5.id shouldBe MD }
        test(SHA) { Hash.SHA_1.id shouldBe SHA }
        test(SHA2) { Hash.SHA_256.id shouldBe SHA2 }
    }
}) {
    companion object {
        const val MD = "MD5"
        const val SHA = "SHA-1"
        const val SHA2 = "SHA-256"
    }
}
