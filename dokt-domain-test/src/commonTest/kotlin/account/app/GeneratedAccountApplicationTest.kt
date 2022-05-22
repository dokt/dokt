package biz.bank.account.app

import app.dokt.domain.test.TestAggregate
import biz.bank.account.*

private val serializer = Account.serializer()

interface AccountCommands {
    fun deposit(amount: Euros): Euros

    fun freeze()

    fun withdraw(amount: Euros): Euros
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
