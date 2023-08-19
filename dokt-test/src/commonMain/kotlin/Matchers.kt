package app.dokt.test

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.*
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Boolean?.shouldBeTrue() {
    shouldNotBeNull()
    shouldBeTrue()
}

inline infix fun <reified T> T.jsonShouldBe(expected: String) = Json.encodeToString(this) shouldBe expected

infix fun List<String>?.linesShouldBe(expected: String?) {
    if (expected == null) shouldBeNull()
    else {
        shouldNotBeNull()
        joinToString("\n") shouldBe expected
    }
}