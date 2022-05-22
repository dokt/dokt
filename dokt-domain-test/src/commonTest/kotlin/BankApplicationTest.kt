package biz.bank

import app.dokt.app.*
import biz.bank.account.*
import biz.bank.account.app.AccountService
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BankApplicationTest : FunSpec({
    context("AccountService") {
        test("deposit") {
            BankTestApplication()
            AccountService.deposit(to, 1.euro) shouldBe 1.euro
        }
    }
}) {
    companion object {
        val to = To(object : UserId {}, Iban("FI123"))
    }
}
