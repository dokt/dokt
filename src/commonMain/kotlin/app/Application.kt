package app.dokt.app

abstract class Application {
    protected open val eventBus: EventBus by lazy { LocalEventBus(eventStore) }

    protected open val eventStore: EventStore by lazy { InMemEventStore }

    init {
        init()
    }

    protected open fun initDomainServices() {}

    protected open fun initEventHandlers() {}

    protected abstract fun initRepositories()

    private fun init() {
        initRepositories()
        initDomainServices()
        bus = eventBus
        initEventHandlers()
    }
}
