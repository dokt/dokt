package biz.bank

import app.dokt.app.To
import app.dokt.domain.event.UserId
import biz.bank.account.Iban
import biz.bank.account.app.AccountService
import biz.bank.account.euro
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BankApplicationTest : FunSpec({
    BankTestApplication()

    context("AccountService") {
        test("deposit") {
            AccountService.deposit(to, 1.euro) shouldBe 1.euro
        }
    }
}) {
    companion object {
        val to = To(object : UserId {}, Iban("FI123"))
    }
}
