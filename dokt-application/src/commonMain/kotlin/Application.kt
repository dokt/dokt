package app.dokt.app

import app.dokt.infra.Logger

open class Application(func: () -> Unit) : Logger(func) {
    protected open val eventBus: EventBus by lazy { LocalEventBus(eventStore) }

    protected open val eventStore: EventStore by lazy { InMemEventStore }

    init {
        init()
    }

    protected open fun initDomainServices() {}

    protected open fun initEventHandlers() {}

    protected open fun initRepositories() {}

    private fun init() {
        initRepositories()
        initDomainServices()
        bus = eventBus
        initEventHandlers()
    }
}
