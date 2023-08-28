@file:Suppress("unused")

package biz.bank.account

import app.dokt.domain.event.RootEvent
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

sealed interface AccountEvent : RootEvent

@JvmInline
@Serializable
value class Deposited(val amount: Euros) : AccountEvent

@Serializable
data object Frozen : AccountEvent

@JvmInline
@Serializable
value class Withdrawn(val amount: Euros) : AccountEvent
