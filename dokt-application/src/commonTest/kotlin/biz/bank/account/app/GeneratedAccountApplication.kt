@file:Suppress("unused")

package biz.bank.account.app

import app.dokt.app.*
import biz.bank.account.*

class AccountAggregate(number: Iban) : Aggregate<Account, Iban, AccountEvent>(number), Events {
    override fun AccountEvent.apply() = when (this) {
        is Deposited -> root.deposited(amount)
        is Frozen -> root.frozen()
        is Withdrawn -> root.withdrawn(amount)
    }

    override fun create() = Account(id).also { it.emit = this }

    override fun deposited(amount: Euros) {
        root.deposited(amount)
        add(Deposited(amount))
    }

    override fun frozen() {
        root.frozen()
        add(Frozen)
    }

    override fun withdrawn(amount: Euros) {
        root.withdrawn(amount)
        add(Withdrawn(amount))
    }
}

object AccountService : AggregateApplicationService<Account, Iban, AccountEvent>(Account::class) {
    suspend fun deposit(to: To<Iban>, amount: Euros) = tx(to) { deposit(amount) }

    suspend fun withdraw(to: To<Iban>, amount: Euros) = tx(to) { withdraw(amount) }
}
