package app.dokt.test

import io.mockk.mockk

/** Creates a relaxed mock object. */
inline fun <reified T : Any> dummy(test: T.() -> Unit) = mockk<T>(relaxed = true).test()
