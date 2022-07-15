package app.dokt.app

import app.dokt.domain.event.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainInOrder
import kotlinx.serialization.Serializable

@Serializable
object FooDomainEvent : DomainEvent
object BarDomainEvent : DomainEvent
object User : UserId
const val rootId = 1
val fooEventMessage = EventMessage(User, rootId, 1u, 1u, FooDomainEvent)
val barEventMessage = EventMessage(User, rootId, 2u, 2u, BarDomainEvent)
val fooAndBarEventMessages = listOf(fooEventMessage, barEventMessage)

class AggregateTest : FunSpec({
    xtest("add") {}
    xtest("apply") {}
    xtest("commit") {}
    xtest("rollback") {}
})

class ApplicationServiceTest : FunSpec({
    xtest("tx") {}
})

class CachingAggregateRepositoryTest : FunSpec({
    xtest("get") {}
})

class EventStoreRepositoryTest : FunSpec({
    xtest("get") {}
})

class InMemEventStoreTest : FunSpec({
    test("add") {
        InMemEventStore.clear()
        InMemEventStore.add(listOf(fooEventMessage))
        InMemEventStore.add(listOf(barEventMessage))
        InMemEventStore.get<Int, DomainEvent>(rootId) shouldContainInOrder fooAndBarEventMessages
    }
})

class LocalEventBusTest : FunSpec({
    test("eventStore") {
        InMemEventStore.clear()
        LocalEventBus(InMemEventStore).dispatch(fooAndBarEventMessages)
        InMemEventStore.get<Int, DomainEvent>(rootId) shouldContainInOrder fooAndBarEventMessages
    }
})
