@file:Suppress("unused")

package biz.bank.account

import app.dokt.domain.test.Arranger
import app.dokt.domain.test.TestAggregate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.FunSpecContainerScope

private val serializer = Account.serializer()

interface AccountCommands {
    fun deposit(amount: Euros): Euros

    fun freeze()

    fun withdraw(amount: Euros): Euros
}


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

class AccountTestAggregate(root: Account)
    : TestAggregate<Account, Events, AccountEvent>(root, serializer), AccountCommands, Events {
    //region Commands
    override fun deposit(amount: Euros) = command.deposit(amount)

    override fun freeze() = command.freeze()

    override fun withdraw(amount: Euros) = command.withdraw(amount)
    //endregion

    //region Events
    override fun deposited(amount: Euros) = apply(Deposited(amount)) { deposited(amount) }

    override fun frozen() = apply(Frozen) { frozen() }

    override fun withdrawn(amount: Euros) = apply(Withdrawn(amount)) { withdrawn(amount) }
    //endregion
}
