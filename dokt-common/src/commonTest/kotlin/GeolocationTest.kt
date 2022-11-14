package app.dokt.common

import app.dokt.test.jsonShouldBe
import io.kotest.core.spec.style.FunSpec

class GeolocationTest : FunSpec({
    context("toJson") {
        test("Lombard Street 12") {
            Geolocation("Lombard Street", 12) jsonShouldBe """{"rt":"Lombard Street","no":12}"""
        }
    }
})