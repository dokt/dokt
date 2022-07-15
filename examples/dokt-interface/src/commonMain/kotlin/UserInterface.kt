@file:Suppress("unused")

package app.dokt.ui

import app.dokt.app.*

interface UserInterface<A : Application> {
    val application: A

    val arguments: Array<String>

    fun start()

    fun stop()
}
