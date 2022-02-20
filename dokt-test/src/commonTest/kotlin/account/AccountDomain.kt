package biz.bank.account

import app.dokt.Root
import kotlinx.serialization.Serializable

@Serializable
data class Euros(val cent: UInt = 0u) {
    operator fun compareTo(other: Euros) = cent.compareTo(other.cent)

    operator fun minus(other: Euros) = Euros(cent - other.cent)

    operator fun plus(other: Euros) = Euros(cent + other.cent)
}

val Int.euro get() = Euros((this * 100).toUInt())

interface Events {
    fun deposited(amount: Euros)

    fun frozen()

    fun withdrawn(amount: Euros)
}

class Denied : Exception()

class NoFunds : Exception()

@Serializable
data class Iban(val country: String, val number: Long) {
    constructor(value: String) : this(value.substring(0..1), value.substring(4).toLong())
}

@Serializable
class Account(val number: Iban) : Root<Events>(), Events {
    var balance = Euros()
        private set

    var frozen = false
        private set

    fun deposit(amount: Euros): Euros {
        if (frozen) throw Denied()
        emit.deposited(amount)
        return balance
    }

    fun freeze() {
        if (!frozen) emit.frozen()
    }

    fun withdraw(amount: Euros): Euros {
        if (frozen) throw Denied()
        if (balance < amount) throw NoFunds()
        emit.withdrawn(amount)
        return balance
    }

    override fun deposited(amount: Euros) { balance += amount }

    override fun frozen() { frozen = true }

    override fun withdrawn(amount: Euros) { balance -= amount }
}
