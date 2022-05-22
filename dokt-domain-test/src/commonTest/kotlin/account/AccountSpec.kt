package biz.bank.account

import app.dokt.domain.test.Arranger
import biz.bank.account.app.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.FunSpecContainerScope

abstract class AccountSpec(body: AccountSpec.() -> Unit, private val testNumber: Iban) : FunSpec() {
    init { body() }

    val account get() =
        Arranger<AccountCommands, Account, Events, AccountEvent>(AccountTestAggregate(Account(testNumber)))()

    fun account(number: Iban = testNumber, apply: (Events.() -> Unit)? = null) =
        Arranger<AccountCommands, Account, Events, AccountEvent>(AccountTestAggregate(Account(number)))(apply)

    fun deposit(test: suspend FunSpecContainerScope.() -> Unit) = context("deposit", test)

    fun freeze(test: suspend FunSpecContainerScope.() -> Unit) = context("freeze", test)

    fun withdraw(test: suspend FunSpecContainerScope.() -> Unit) = context("withdraw", test)
}
