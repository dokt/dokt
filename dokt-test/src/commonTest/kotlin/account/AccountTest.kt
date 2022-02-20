package biz.bank.account

import io.kotest.matchers.shouldBe

@Suppress("UNUSED")
class AccountTest : AccountSpec({
    freeze {
        test("Frozen") {
            account().act { freeze() }.emits(Frozen).assert {
                number shouldBe iban
                frozen shouldBe true
            }
        }
    }
    deposit {
        test("balance") {
            account().act { deposit(euro) }.assert { balance shouldBe euro }
        }
        test("deposited") {
            account().act { deposit(euro) }.emits(deposited)
        }
        test("Frozen") {
            account { frozen() }.act { deposit(euro) }.throws<Denied>()
        }
        test("return") {
            account().act { deposit(euro) }.returns(euro)
        }
    }
    withdraw {
        test("NoFunds") {
            account().act { withdraw(euro) }.throws<NoFunds>()
        }
    }
}, iban) {
    companion object {
        val euro = 1.euro
        val deposited = Deposited(euro)
        val iban = Iban("FI001")
    }
}
