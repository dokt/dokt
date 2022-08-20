@file:Suppress("unused")

package app.dokt.common

fun <T> Boolean?.isFalse(block: () -> T?) = if (this == false) block() else null
fun <T> Boolean?.isNull(block: () -> T?) = if (this == null) block() else null
fun <T> Boolean?.isTrue(block: () -> T?) = if (this == true) block() else null
