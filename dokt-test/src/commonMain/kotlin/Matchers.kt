package app.dokt.test

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull

fun Boolean?.shouldBeTrue() {
    shouldNotBeNull()
    shouldBeTrue()
}

