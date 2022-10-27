package biz.bank

import app.dokt.app.*
import biz.bank.account.Iban
import biz.bank.account.app.AccountAggregate

class BankTestApplication : Application({}) {
    override val eventStore get() = InMemEventStore.also { it.clear() }

    override fun initRepositories() {
        AggregateRepository + EventStoreRepository({ number: Iban -> AccountAggregate(number) })
    }
}
