package biz.bank

import app.dokt.app.AggregateRepository
import app.dokt.app.Application
import app.dokt.app.EventStoreRepository
import app.dokt.app.InMemEventStore
import biz.bank.account.Iban
import biz.bank.account.app.AccountAggregate

class BankTestApplication : Application({}) {
    override val eventStore get() = InMemEventStore.also { it.clear() }

    override fun initRepositories() {
        AggregateRepository + EventStoreRepository({ number: Iban -> AccountAggregate(number) })
    }
}
