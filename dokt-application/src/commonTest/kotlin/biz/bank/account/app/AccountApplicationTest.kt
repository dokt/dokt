package biz.bank.account.app

import biz.bank.account.Iban
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AccountAggregateTest : FunSpec({
    test("version") {
        AccountAggregate(iban).version shouldBe 0u
    }
    test("id") {
        AccountAggregate(iban).id shouldBe iban
    }
}) {
    companion object {
        val iban = Iban("FI001")
    }
}

class AccountServiceTest : FunSpec({
    test("version") {
        //AccountService.deposit()
    }
    test("id") {
        AccountAggregate(iban).id shouldBe iban
    }
}) {
    companion object {
        val iban = Iban("FI001")
    }
}
